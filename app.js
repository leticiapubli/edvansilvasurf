/**
 * Onda Surf - Web Application & PWA
 * Ready for Vercel, Localhost and Mobile Browsers
 */

// Storage Keys
const STORAGE_SLOTS = 'ondasurf_slots';
const STORAGE_BOOKINGS = 'ondasurf_bookings';
const STORAGE_USER = 'ondasurf_username';
const STORAGE_DATE = 'ondasurf_date';

// Initial Default Schedule
const DEFAULT_SLOTS = [
  {
    id: 1,
    time: '06:30',
    period: 'Primeira Caída',
    instructor: 'Instrutor Lucas',
    maxSpots: 4,
    isWeatherCancelled: false,
    weatherCancelReason: '',
    isClosedClass: false,
    closedClassName: '',
    date: '02/11',
    orderIndex: 1
  },
  {
    id: 2,
    time: '07:30',
    period: 'Manhã',
    instructor: 'Instrutora Maya',
    maxSpots: 4,
    isWeatherCancelled: false,
    weatherCancelReason: '',
    isClosedClass: false,
    closedClassName: '',
    date: '02/11',
    orderIndex: 2
  },
  {
    id: 3,
    time: '11:30',
    period: 'Meio-Dia',
    instructor: 'Instrutor Pedro',
    maxSpots: 4,
    isWeatherCancelled: false,
    weatherCancelReason: '',
    isClosedClass: false,
    closedClassName: '',
    date: '02/11',
    orderIndex: 3
  },
  {
    id: 4,
    time: '14:30',
    period: 'Tarde',
    instructor: 'Instrutor Gabriel',
    maxSpots: 4,
    isWeatherCancelled: false,
    weatherCancelReason: '',
    isClosedClass: false,
    closedClassName: '',
    date: '02/11',
    orderIndex: 4
  }
];

const DEFAULT_BOOKINGS = [
  { id: 101, slotId: 1, studentName: 'Felipe M.' },
  { id: 102, slotId: 2, studentName: 'Marina S.' },
  { id: 103, slotId: 2, studentName: 'Lucas P.' },
  { id: 104, slotId: 3, studentName: 'Thiago M.' },
  { id: 105, slotId: 3, studentName: 'Carolina R.' },
  { id: 106, slotId: 3, studentName: 'Gabriel F.' },
  { id: 107, slotId: 3, studentName: 'Beatriz L.' }
];

// App State
let state = {
  slots: [],
  bookings: [],
  userName: '',
  isAdminMode: false,
  displayDate: '',
  modal: null
};

// Initialize Application
function initApp() {
  loadState();
  setupEventListeners();
  renderApp();
  registerServiceWorker();
}

function loadState() {
  try {
    const savedSlots = localStorage.getItem(STORAGE_SLOTS);
    const savedBookings = localStorage.getItem(STORAGE_BOOKINGS);
    const savedUser = localStorage.getItem(STORAGE_USER);
    const savedDate = localStorage.getItem(STORAGE_DATE);

    state.slots = savedSlots ? JSON.parse(savedSlots) : [...DEFAULT_SLOTS];
    state.bookings = savedBookings ? JSON.parse(savedBookings) : [...DEFAULT_BOOKINGS];
    state.userName = savedUser || '';
    state.displayDate = savedDate || getFormattedDateToday();
  } catch (e) {
    console.warn('Erro ao carregar do localStorage, usando padrões:', e);
    state.slots = [...DEFAULT_SLOTS];
    state.bookings = [...DEFAULT_BOOKINGS];
    state.userName = '';
    state.displayDate = getFormattedDateToday();
  }
}

function saveState() {
  localStorage.setItem(STORAGE_SLOTS, JSON.stringify(state.slots));
  localStorage.setItem(STORAGE_BOOKINGS, JSON.stringify(state.bookings));
  localStorage.setItem(STORAGE_USER, state.userName);
  localStorage.setItem(STORAGE_DATE, state.displayDate);
}

function getFormattedDateToday() {
  const options = { weekday: 'long', day: 'numeric', month: 'long' };
  const d = new Date().toLocaleDateString('pt-BR', options);
  return d.charAt(0).toUpperCase() + d.slice(1);
}

// Render Core UI
function renderApp() {
  const app = document.getElementById('app');
  if (!app) return;

  const myBookings = state.userName.trim()
    ? state.bookings.filter(b => b.studentName.trim().toLowerCase() === state.userName.trim().toLowerCase())
    : [];

  app.innerHTML = `
    <!-- Header Hero -->
    <header class="header-hero">
      <div class="header-top">
        <div class="brand-row">
          <div class="brand-icon">🏄</div>
          <div>
            <div class="brand-title">ONDA SURF</div>
            <div class="brand-subtitle">Escola de Surf • Aulas de Hoje</div>
          </div>
        </div>
        <button class="admin-badge-btn ${state.isAdminMode ? 'active' : ''}" id="btn-admin-toggle">
          ${state.isAdminMode ? '🛡️ Admin (Sair)' : '🛡️ Admin'}
        </button>
      </div>

      <div class="header-date-row">
        <div class="date-sub">HOJE NO MAR</div>
        <div class="date-main">${escapeHtml(state.displayDate)}</div>
      </div>

      <!-- Ocean Conditions -->
      <div class="conditions-card">
        <div class="condition-item">
          <div class="condition-label">Maré</div>
          <div class="condition-val">0.4m (Seca)</div>
        </div>
        <div class="condition-item">
          <div class="condition-label">Vento</div>
          <div class="condition-val">12 nós L</div>
        </div>
        <div class="condition-item">
          <div class="condition-label">Ondulação</div>
          <div class="condition-val">1.2m - 1.5m</div>
        </div>
        <div class="condition-item">
          <div class="condition-label">Água</div>
          <div class="condition-val">28°C Boa</div>
        </div>
      </div>

      <!-- User Chip -->
      <div class="user-chip-row" id="btn-user-profile">
        <div class="user-chip-info">
          <span>👤</span>
          <span>${state.userName.trim() ? 'Surfista: ' + escapeHtml(state.userName) : 'Toque para identificar seu nome'}</span>
        </div>
        <span class="user-chip-action">${state.userName.trim() ? 'Alterar' : 'Entrar'}</span>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Admin Toolbar if Logged in -->
      ${state.isAdminMode ? `
        <div class="admin-toolbar">
          <div class="admin-toolbar-title">
            <span>⚙️ Painel do Instrutor Ativo</span>
          </div>
          <div class="admin-toolbar-actions">
            <button class="btn-admin btn-admin-primary" id="btn-admin-add-slot">+ Horário</button>
            <button class="btn-admin btn-admin-outline" id="btn-admin-reset">Restaurar</button>
            <button class="btn-admin btn-admin-tertiary" id="btn-admin-import">📥 Importar</button>
          </div>
        </div>
      ` : ''}

      <!-- Dedicated Student Bookings Panel -->
      ${state.userName.trim() ? `
        <div class="student-panel">
          <div class="student-panel-header">
            <div class="student-panel-title">
              <span>🏄 Suas Aulas Confirmadas</span>
            </div>
            <div class="student-panel-badge">
              ${myBookings.length} ${myBookings.length === 1 ? 'CONFIRMADA' : 'CONFIRMADAS'}
            </div>
          </div>
          ${myBookings.length > 0 ? `
            <div class="student-bookings-list">
              ${myBookings.map(b => {
                const s = state.slots.find(slot => slot.id === b.slotId);
                const time = s ? s.time : '--:--';
                const instructor = s ? s.instructor : 'Escola';
                return `
                  <div class="student-booking-item">
                    <div>
                      <div class="booking-item-time">${escapeHtml(time)}</div>
                      <div class="booking-item-meta">${escapeHtml(instructor)}</div>
                    </div>
                    <button class="btn-cancel-small" data-cancel-booking-id="${b.id}" data-slot-id="${b.slotId}">
                      Cancelar
                    </button>
                  </div>
                `;
              }).join('')}
            </div>
          ` : `
            <div style="font-size: 12px; color: var(--text-secondary);">
              Você ainda não confirmou presença em nenhuma aula hoje. Escolha um ou mais horários abaixo e clique em <strong>"Quero Ir"</strong>.
            </div>
          `}
        </div>
      ` : ''}

      <!-- Info Legend Banner -->
      <div class="info-banner">
        <span class="info-banner-icon">ℹ️</span>
        <div class="info-banner-text">
          <strong>Inscrição flexível em múltiplos horários</strong>
          Você pode se inscrever em mais de uma aula no mesmo dia se houver vagas (limite estrito de 4 surfistas por turma).
          <div class="info-legend">
            Feedback visual: 🟢 Disponível (0-2) • 🟠 Quase Lotado (3/4) • 🔴 Esgotado (4/4)
          </div>
        </div>
      </div>

      <!-- Schedule Section Title -->
      <div class="section-header-row">
        <div class="section-title">GRADE DE HORÁRIOS: ${escapeHtml(state.displayDate)}</div>
        <div class="section-subtitle">${state.slots.length} aulas hoje</div>
      </div>

      <!-- Slots List -->
      ${state.slots.map(slot => renderSlotCard(slot)).join('')}

      <!-- Footer Section -->
      <footer class="footer-section">
        <div class="footer-card">
          <div class="footer-title">ACOMPANHE A ESCOLA DE SURF</div>
          <div class="footer-social-row">
            <a href="https://www.instagram.com/edvansilvasurfschool/?hl=pt-br" target="_blank" rel="noopener" class="social-btn social-instagram">
              📸 Instagram
            </a>
            <a href="https://wa.me/558581781257" target="_blank" rel="noopener" class="social-btn social-whatsapp">
              💬 WhatsApp
            </a>
          </div>
          <div class="footer-credits">Desenvolvido pela LeticiaPubli 2026</div>
        </div>
      </footer>
    </main>

    <!-- Modal Container -->
    <div id="modal-container"></div>

    <!-- Toast Notification -->
    <div id="toast" class="toast-container"></div>
  `;

  attachDynamicListeners();
}

// Render Slot Card
function renderSlotCard(slot) {
  const slotBookings = state.bookings.filter(b => b.slotId === slot.id);
  const bookedCount = slotBookings.length;
  const totalSpots = slot.maxSpots || 4;
  const availableSpots = Math.max(0, totalSpots - bookedCount);
  const isFull = bookedCount >= totalSpots;
  const isAlmostFull = bookedCount === 3;
  const isClosed = !!slot.isClosedClass;
  const isCancelled = !!slot.isWeatherCancelled;

  const isUserBooked = state.userName.trim()
    ? slotBookings.some(b => b.studentName.trim().toLowerCase() === state.userName.trim().toLowerCase())
    : false;

  // Visual Class Logic
  let cardClass = '';
  let timeClass = '';
  let badgeClass = 'badge-teal';
  let badgeText = `${availableSpots} VAGAS`;
  let boxClass = '';
  let countClass = '';

  if (isCancelled) {
    cardClass = 'card-cancelled';
    timeClass = 'color-weather';
    badgeClass = 'badge-weather';
    badgeText = 'CANCELADA';
  } else if (isClosed) {
    cardClass = 'card-purple';
    timeClass = 'color-purple';
    badgeClass = 'badge-purple';
    badgeText = 'AULA FECHADA';
    boxClass = 'box-purple';
    countClass = 'count-purple';
  } else if (isUserBooked) {
    cardClass = 'card-user';
    badgeClass = 'badge-user';
    badgeText = 'SUA VAGA';
  } else if (isFull) {
    cardClass = 'card-red';
    timeClass = 'color-red';
    badgeClass = 'badge-red';
    badgeText = 'LOTADA (4/4)';
    boxClass = 'box-red';
    countClass = 'count-red';
  } else if (isAlmostFull) {
    cardClass = 'card-orange';
    timeClass = 'color-orange';
    badgeClass = 'badge-orange';
    badgeText = 'QUASE LOTADA (3/4)';
    boxClass = 'box-orange';
    countClass = 'count-orange';
  }

  // 4 Visual Spots Array
  const spotPillsHtml = [];
  for (let i = 0; i < totalSpots; i++) {
    const booking = slotBookings[i];
    const isOccupied = !!booking;
    const isCurrentUserSpot = isOccupied && state.userName.trim() &&
      booking.studentName.trim().toLowerCase() === state.userName.trim().toLowerCase();

    let pillClass = 'spot-pill';
    let pillText = 'Livre';

    if (isCurrentUserSpot) {
      pillClass += ' occupied-user';
      pillText = '✓ Você';
    } else if (isOccupied) {
      if (isFull) {
        pillClass += ' occupied-red';
      } else if (isAlmostFull) {
        pillClass += ' occupied-orange';
      } else {
        pillClass += ' occupied';
      }
      pillText = booking.studentName.slice(0, 8);
    } else {
      if (isAlmostFull) {
        pillClass += ' empty-orange';
        pillText = '1 Vaga!';
      }
    }

    spotPillsHtml.push(`<div class="${pillClass}" title="${isOccupied ? escapeHtml(booking.studentName) : 'Vaga livre'}">${escapeHtml(pillText)}</div>`);
  }

  // Action Button Logic
  let actionButtonHtml = '';
  if (isClosed) {
    actionButtonHtml = `
      <button class="btn-action btn-disabled-purple" disabled>
        🔒 Aula Exclusiva / Fechada
      </button>
    `;
  } else if (isCancelled) {
    actionButtonHtml = `
      <button class="btn-action btn-disabled" disabled>
        ⚠️ Aula Cancelada por Tempo Ruim
      </button>
    `;
  } else if (isUserBooked) {
    actionButtonHtml = `
      <div style="display: flex; flex-direction: column; gap: 6px;">
        <div style="font-size: 12px; font-weight: 700; color: var(--primary); text-align: center;">
          ✓ Presença confirmada! Nos vemos na areia.
        </div>
        <button class="btn-action btn-cancel-presence" data-action="cancel" data-slot-id="${slot.id}">
          ✕ Cancelar Presença
        </button>
      </div>
    `;
  } else if (isFull) {
    actionButtonHtml = `
      <button class="btn-action btn-disabled" disabled>
        Turma Cheia (4/4 alunos)
      </button>
    `;
  } else {
    actionButtonHtml = `
      <button class="btn-action btn-want-to-go" data-action="book" data-slot-id="${slot.id}">
        🏄 Quero Ir
      </button>
    `;
  }

  return `
    <div class="slot-card ${cardClass}" data-slot-id="${slot.id}">
      <div class="slot-header">
        <div class="slot-time-col">
          <div class="slot-time ${timeClass}">${escapeHtml(slot.time)}</div>
          <div>
            <div class="slot-meta-title">${escapeHtml(slot.period || 'Manhã')}</div>
            <div class="slot-meta-instructor">${escapeHtml(slot.instructor || 'Instrutor')}</div>
          </div>
        </div>
        <div class="badge ${badgeClass}">${escapeHtml(badgeText)}</div>
      </div>

      <!-- Capacity Box -->
      <div class="capacity-box ${boxClass}">
        <div class="capacity-header">
          <span>Vagas em tempo real:</span>
          <span class="capacity-count ${countClass}">
            ${bookedCount}/${totalSpots} preenchidas
            ${isAlmostFull && !isCancelled && !isClosed ? '• Resta 1 vaga!' : ''}
            ${isFull && !isCancelled && !isClosed ? '• Esgotado' : ''}
          </span>
        </div>
        <div class="spots-row">
          ${spotPillsHtml.join('')}
        </div>
      </div>

      <!-- Closed Class Box if marked by instructor -->
      ${isClosed ? `
        <div class="alert-box alert-box-purple">
          <span style="font-size: 20px;">🔒</span>
          <div>
            <div class="alert-title">Aula Fechada / Particular</div>
            <div class="alert-desc">
              ${slot.closedClassName.trim() ? 'Reservado para: ' + escapeHtml(slot.closedClassName) : 'Horário reservado exclusivamente pelo instrutor.'}
            </div>
          </div>
        </div>
      ` : ''}

      <!-- Weather Alert Box if marked -->
      ${isCancelled ? `
        <div class="alert-box alert-box-weather">
          <span style="font-size: 20px;">⚠️</span>
          <div>
            <div class="alert-title">Cancelada por tempo ruim</div>
            <div class="alert-desc">${escapeHtml(slot.weatherCancelReason || 'Condições do mar desfavoráveis.')}</div>
          </div>
        </div>
      ` : ''}

      <!-- Action Button -->
      ${actionButtonHtml}

      <!-- Instructor controls if Admin mode is on -->
      ${state.isAdminMode ? `
        <div class="instructor-slot-tools">
          <div class="instructor-tools-title">⚙️ Gerenciar Aula das ${escapeHtml(slot.time)}:</div>
          <div class="instructor-tools-btns">
            <button class="btn-tool" data-admin-action="students" data-slot-id="${slot.id}">👥 Alunos</button>
            <button class="btn-tool" data-admin-action="weather" data-slot-id="${slot.id}">🌦️ Tempo</button>
            <button class="btn-tool" data-admin-action="clear" data-slot-id="${slot.id}">🗑️ Limpar</button>
          </div>
        </div>
      ` : ''}
    </div>
  `;
}

// Setup Event Listeners
function setupEventListeners() {
  document.addEventListener('click', (e) => {
    // Admin Toggle
    if (e.target.closest('#btn-admin-toggle')) {
      if (state.isAdminMode) {
        state.isAdminMode = false;
        showToast('Sessão de Admin encerrada');
        renderApp();
      } else {
        openModal('admin-login');
      }
      return;
    }

    // User Profile Chip
    if (e.target.closest('#btn-user-profile')) {
      openModal('user-name');
      return;
    }

    // Admin Toolbar Actions
    if (e.target.closest('#btn-admin-add-slot')) {
      openModal('add-slot');
      return;
    }
    if (e.target.closest('#btn-admin-reset')) {
      if (confirm('Deseja restaurar a grade de horários padrão de hoje?')) {
        state.slots = JSON.parse(JSON.stringify(DEFAULT_SLOTS));
        saveState();
        showToast('Grade padrão restaurada!');
        renderApp();
      }
      return;
    }
    if (e.target.closest('#btn-admin-import')) {
      openModal('import-slots');
      return;
    }

    // Book Slot
    const bookBtn = e.target.closest('[data-action="book"]');
    if (bookBtn) {
      const slotId = parseInt(bookBtn.getAttribute('data-slot-id'), 10);
      handleBookSlot(slotId);
      return;
    }

    // Cancel Presence from Card
    const cancelBtn = e.target.closest('[data-action="cancel"]');
    if (cancelBtn) {
      const slotId = parseInt(cancelBtn.getAttribute('data-slot-id'), 10);
      handleCancelSlotBooking(slotId);
      return;
    }

    // Cancel from Top Student Panel
    const smallCancelBtn = e.target.closest('[data-cancel-booking-id]');
    if (smallCancelBtn) {
      const slotId = parseInt(smallCancelBtn.getAttribute('data-slot-id'), 10);
      handleCancelSlotBooking(slotId);
      return;
    }

    // Instructor Actions inside Slot
    const adminActionBtn = e.target.closest('[data-admin-action]');
    if (adminActionBtn) {
      const action = adminActionBtn.getAttribute('data-admin-action');
      const slotId = parseInt(adminActionBtn.getAttribute('data-slot-id'), 10);
      if (action === 'students') {
        openModal('manage-students', { slotId });
      } else if (action === 'weather') {
        openModal('weather-cancel', { slotId });
      } else if (action === 'clear') {
        if (confirm('Deseja liberar todas as vagas desta aula?')) {
          state.bookings = state.bookings.filter(b => b.slotId !== slotId);
          saveState();
          showToast('Vagas deste horário liberadas');
          renderApp();
        }
      }
      return;
    }

    // Modal Close
    if (e.target.closest('[data-modal-close]') || e.target.classList.contains('modal-overlay')) {
      closeModal();
      return;
    }
  });
}

function attachDynamicListeners() {
  // Attached globally
}

// User Actions
function handleBookSlot(slotId) {
  if (!state.userName.trim()) {
    openModal('user-name', { targetSlotId: slotId });
    return;
  }

  const slot = state.slots.find(s => s.id === slotId);
  if (!slot) return;

  if (slot.isClosedClass) {
    showToast('Este horário é uma aula particular fechada.');
    return;
  }

  if (slot.isWeatherCancelled) {
    showToast('Esta aula foi cancelada por condições do tempo.');
    return;
  }

  const slotBookings = state.bookings.filter(b => b.slotId === slotId);
  if (slotBookings.length >= (slot.maxSpots || 4)) {
    showToast('Esta turma já atingiu a lotação máxima (4 alunos).');
    return;
  }

  // Check if already in this slot
  const alreadyBooked = slotBookings.some(b => b.studentName.trim().toLowerCase() === state.userName.trim().toLowerCase());
  if (alreadyBooked) {
    showToast('Você já confirmou presença nesta aula!');
    return;
  }

  // Add booking
  state.bookings.push({
    id: Date.now(),
    slotId: slotId,
    studentName: state.userName.trim()
  });

  saveState();
  showToast(`Presença confirmada para as ${slot.time}! 🏄`);
  renderApp();
}

function handleCancelSlotBooking(slotId) {
  const slot = state.slots.find(s => s.id === slotId);
  const timeText = slot ? `das ${slot.time}` : '';

  if (!confirm(`Deseja cancelar sua presença na aula ${timeText}? Sua vaga será liberada imediatamente para outro aluno.`)) {
    return;
  }

  state.bookings = state.bookings.filter(b => {
    return !(b.slotId === slotId && b.studentName.trim().toLowerCase() === state.userName.trim().toLowerCase());
  });

  saveState();
  showToast(`Presença cancelada na aula ${timeText}. Vaga liberada.`);
  renderApp();
}

// Modals System
function openModal(modalType, data = {}) {
  const container = document.getElementById('modal-container');
  if (!container) return;

  let modalHtml = '';

  if (modalType === 'admin-login') {
    modalHtml = `
      <div class="modal-overlay">
        <div class="modal-card">
          <div class="modal-header">
            <span class="modal-icon">🛡️</span>
            <div>
              <div class="modal-title">Acesso Restrito • Admin</div>
              <div class="modal-desc">Digite o usuário e senha do instrutor</div>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">Usuário</label>
            <input type="text" id="admin-user-input" class="form-input" placeholder="admin" autocomplete="off" />
          </div>
          <div class="form-group">
            <label class="form-label">Senha</label>
            <input type="password" id="admin-pass-input" class="form-input" placeholder="••••••••" />
          </div>
          <div id="admin-login-error" style="color: var(--weather-text); font-size: 12px; font-weight: 700; display: none;"></div>
          <div class="modal-actions">
            <button class="btn-secondary" data-modal-close>Cancelar</button>
            <button class="btn-primary" id="btn-admin-login-submit">Entrar como Admin</button>
          </div>
        </div>
      </div>
    `;
  } else if (modalType === 'user-name') {
    modalHtml = `
      <div class="modal-overlay">
        <div class="modal-card">
          <div class="modal-header">
            <span class="modal-icon">🏄</span>
            <div>
              <div class="modal-title">Identificação do Aluno</div>
              <div class="modal-desc">Informe seu nome para agendar horários</div>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">Seu Nome Completo ou Como Prefere ser Chamado</label>
            <input type="text" id="user-name-input" class="form-input" value="${escapeHtml(state.userName)}" placeholder="Ex: Felipe Silva" />
          </div>
          <div class="modal-actions">
            <button class="btn-secondary" data-modal-close>Cancelar</button>
            <button class="btn-primary" id="btn-user-name-submit" data-target-slot-id="${data.targetSlotId || ''}">Confirmar</button>
          </div>
        </div>
      </div>
    `;
  } else if (modalType === 'import-slots') {
    modalHtml = `
      <div class="modal-overlay">
        <div class="modal-card">
          <div class="modal-header">
            <span class="modal-icon">📥</span>
            <div>
              <div class="modal-title">Importar Horários Rápido</div>
              <div class="modal-desc">Cole a lista de texto exata com os horários</div>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">Cole o texto aqui:</label>
            <textarea id="import-text-input" class="form-input form-textarea" placeholder="Horario das aulas&#10;01/11&#10;📍professor Edvan&#10;🏄🏼♀️6:30 as 7:30&#10;🏄🏼♀️7:30 as 8:30&#10;🏄🏼♂️8:30 as 9:30"></textarea>
          </div>
          <div style="font-size: 11px; color: var(--text-secondary); line-height: 1.4;">
            Ao clicar em <strong>"Substituir tudo"</strong>, os horários e inscrições anteriores do dia serão substituídos pelos novos horários extraídos do texto.
          </div>
          <div class="modal-actions">
            <button class="btn-secondary" data-modal-close>Cancelar</button>
            <button class="btn-primary" id="btn-import-submit">Substituir tudo</button>
          </div>
        </div>
      </div>
    `;
  } else if (modalType === 'manage-students') {
    const slot = state.slots.find(s => s.id === data.slotId);
    if (!slot) return;
    const slotBookings = state.bookings.filter(b => b.slotId === slot.id);

    modalHtml = `
      <div class="modal-overlay">
        <div class="modal-card">
          <div class="modal-header">
            <span class="modal-icon">👥</span>
            <div>
              <div class="modal-title">Gerenciar Horário • ${escapeHtml(slot.time)}</div>
              <div class="modal-desc">${slotBookings.length}/${slot.maxSpots || 4} vagas preenchidas</div>
            </div>
          </div>

          <!-- Section 1: Closed Class / Private Class -->
          <div class="closed-class-box-editor">
            <label class="checkbox-row">
              <input type="checkbox" id="manage-closed-class-check" ${slot.isClosedClass ? 'checked' : ''} />
              <span>🔒 Marcar como 'Aula Fechada / Privada'</span>
            </label>
            <div id="closed-class-name-container" style="${slot.isClosedClass ? '' : 'display: none;'}">
              <div style="font-size: 11px; color: var(--text-primary); margin-bottom: 4px;">
                Nome(s) do(s) aluno(s) da Aula Fechada:
              </div>
              <input type="text" id="manage-closed-name-input" class="form-input" style="color: #000000;" value="${escapeHtml(slot.closedClassName || '')}" placeholder="Ex: Aula Particular do João" />
            </div>
          </div>

          <!-- Section 2: Enrolled Students List -->
          <div>
            <div style="font-size: 12px; font-weight: 800; color: var(--primary); margin-bottom: 6px;">
              Alunos Inscritos:
            </div>
            ${slotBookings.length > 0 ? `
              <div style="display: flex; flex-direction: column; gap: 6px;">
                ${slotBookings.map((b, idx) => `
                  <div style="display: flex; align-items: center; justify-content: space-between; background: var(--surface-variant); padding: 8px 12px; border-radius: 10px;">
                    <span style="font-size: 13px; font-weight: 700;">${idx + 1}. ${escapeHtml(b.studentName)}</span>
                    <button class="btn-cancel-small" data-remove-booking-id="${b.id}" style="padding: 4px 8px;">Remover</button>
                  </div>
                `).join('')}
              </div>
            ` : `
              <div style="font-size: 12px; color: var(--text-secondary); background: var(--surface-variant); padding: 10px; border-radius: 8px;">
                Nenhum aluno inscrito nesta turma.
              </div>
            `}
          </div>

          <!-- Section 3: Add Manual Student -->
          ${slotBookings.length < (slot.maxSpots || 4) && !slot.isClosedClass ? `
            <div class="form-group">
              <label class="form-label">Adicionar aluno manualmente:</label>
              <div style="display: flex; gap: 8px;">
                <input type="text" id="manage-new-student-input" class="form-input" placeholder="Nome do aluno" />
                <button class="btn-primary" id="btn-manage-add-student" data-slot-id="${slot.id}" style="padding: 8px 14px;">+ Adicionar</button>
              </div>
            </div>
          ` : ''}

          <div class="modal-actions">
            <button class="btn-secondary" data-modal-close>Cancelar</button>
            <button class="btn-primary" id="btn-manage-save-closed" data-slot-id="${slot.id}">Salvar Alterações</button>
          </div>
        </div>
      </div>
    `;
  } else if (modalType === 'weather-cancel') {
    const slot = state.slots.find(s => s.id === data.slotId);
    if (!slot) return;

    modalHtml = `
      <div class="modal-overlay">
        <div class="modal-card">
          <div class="modal-header">
            <span class="modal-icon">🌦️</span>
            <div>
              <div class="modal-title">Condições do Tempo • ${escapeHtml(slot.time)}</div>
              <div class="modal-desc">Suspender aula por mar revolto ou tempestade</div>
            </div>
          </div>
          <label style="display: flex; align-items: center; gap: 8px; font-size: 14px; font-weight: 800; cursor: pointer;">
            <input type="checkbox" id="weather-check-input" ${slot.isWeatherCancelled ? 'checked' : ''} />
            <span>Marcar aula como CANCELADA por tempo ruim</span>
          </label>
          <div class="form-group">
            <label class="form-label">Motivo do cancelamento:</label>
            <input type="text" id="weather-reason-input" class="form-input" value="${escapeHtml(slot.weatherCancelReason || 'Condições do mar desfavoráveis (vento forte e correnteza).')}" />
          </div>
          <div class="modal-actions">
            <button class="btn-secondary" data-modal-close>Cancelar</button>
            <button class="btn-primary" id="btn-weather-save" data-slot-id="${slot.id}">Salvar Status</button>
          </div>
        </div>
      </div>
    `;
  } else if (modalType === 'add-slot') {
    modalHtml = `
      <div class="modal-overlay">
        <div class="modal-card">
          <div class="modal-header">
            <span class="modal-icon">⏰</span>
            <div>
              <div class="modal-title">Criar Novo Horário</div>
              <div class="modal-desc">Adicione uma turma extra para hoje</div>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">Horário (ex: 15:30 ou 16:00):</label>
            <input type="text" id="new-slot-time" class="form-input" placeholder="16:00" />
          </div>
          <div class="form-group">
            <label class="form-label">Período (ex: Manhã, Tarde, Sunset):</label>
            <input type="text" id="new-slot-period" class="form-input" value="Tarde" />
          </div>
          <div class="form-group">
            <label class="form-label">Instrutor Responsável:</label>
            <input type="text" id="new-slot-instructor" class="form-input" value="Instrutor Edvan" />
          </div>
          <div class="modal-actions">
            <button class="btn-secondary" data-modal-close>Cancelar</button>
            <button class="btn-primary" id="btn-add-slot-submit">Criar Horário</button>
          </div>
        </div>
      </div>
    `;
  }

  container.innerHTML = modalHtml;
  bindModalEvents(modalType, data);
}

function closeModal() {
  const container = document.getElementById('modal-container');
  if (container) container.innerHTML = '';
}

function bindModalEvents(modalType, data) {
  // Admin Login
  if (modalType === 'admin-login') {
    const btn = document.getElementById('btn-admin-login-submit');
    const userInp = document.getElementById('admin-user-input');
    const passInp = document.getElementById('admin-pass-input');
    const errBox = document.getElementById('admin-login-error');

    btn?.addEventListener('click', () => {
      const u = (userInp?.value || '').trim();
      const p = (passInp?.value || '').trim();
      if (u === 'admin' && p === 'surf2026') {
        state.isAdminMode = true;
        closeModal();
        showToast('Login de Admin efetuado com sucesso!');
        renderApp();
      } else {
        if (errBox) {
          errBox.textContent = 'Usuário ou senha incorretos. (Dica: admin / surf2026)';
          errBox.style.display = 'block';
        }
      }
    });
  }

  // User Name
  if (modalType === 'user-name') {
    const btn = document.getElementById('btn-user-name-submit');
    const inp = document.getElementById('user-name-input');
    btn?.addEventListener('click', () => {
      const name = (inp?.value || '').trim();
      if (!name) {
        alert('Por favor digite seu nome.');
        return;
      }
      state.userName = name;
      saveState();
      closeModal();
      showToast(`Olá, ${name}!`);

      const targetSlotId = parseInt(btn.getAttribute('data-target-slot-id'), 10);
      if (targetSlotId) {
        handleBookSlot(targetSlotId);
      } else {
        renderApp();
      }
    });
  }

  // Import Fast Schedule
  if (modalType === 'import-slots') {
    const btn = document.getElementById('btn-import-submit');
    const txtArea = document.getElementById('import-text-input');
    btn?.addEventListener('click', () => {
      const rawText = (txtArea?.value || '').trim();
      if (!rawText) {
        alert('Por favor cole o texto com os horários das aulas.');
        return;
      }
      handleFastImport(rawText);
      closeModal();
    });
  }

  // Manage Students
  if (modalType === 'manage-students') {
    const slotId = data.slotId;
    const closedCheck = document.getElementById('manage-closed-class-check');
    const nameContainer = document.getElementById('closed-class-name-container');
    const nameInput = document.getElementById('manage-closed-name-input');
    const saveBtn = document.getElementById('btn-manage-save-closed');
    const addStudentBtn = document.getElementById('btn-manage-add-student');
    const newStudentInput = document.getElementById('manage-new-student-input');

    closedCheck?.addEventListener('change', () => {
      if (nameContainer) {
        nameContainer.style.display = closedCheck.checked ? 'block' : 'none';
      }
    });

    saveBtn?.addEventListener('click', () => {
      const slot = state.slots.find(s => s.id === slotId);
      if (slot) {
        slot.isClosedClass = !!closedCheck?.checked;
        slot.closedClassName = (nameInput?.value || '').trim();
        saveState();
        showToast(slot.isClosedClass ? 'Marcada como Aula Fechada!' : 'Aula reaberta para inscrições públicas.');
        closeModal();
        renderApp();
      }
    });

    addStudentBtn?.addEventListener('click', () => {
      const name = (newStudentInput?.value || '').trim();
      if (!name) return;
      state.bookings.push({
        id: Date.now(),
        slotId: slotId,
        studentName: name
      });
      saveState();
      showToast(`Aluno ${name} adicionado!`);
      closeModal();
      renderApp();
    });

    // Remove single student buttons
    document.querySelectorAll('[data-remove-booking-id]').forEach(btn => {
      btn.addEventListener('click', () => {
        const bId = parseInt(btn.getAttribute('data-remove-booking-id'), 10);
        state.bookings = state.bookings.filter(b => b.id !== bId);
        saveState();
        showToast('Aluno removido da turma.');
        closeModal();
        renderApp();
      });
    });
  }

  // Weather Cancel
  if (modalType === 'weather-cancel') {
    const slotId = data.slotId;
    const saveBtn = document.getElementById('btn-weather-save');
    const checkInp = document.getElementById('weather-check-input');
    const reasonInp = document.getElementById('weather-reason-input');

    saveBtn?.addEventListener('click', () => {
      const slot = state.slots.find(s => s.id === slotId);
      if (slot) {
        slot.isWeatherCancelled = !!checkInp?.checked;
        slot.weatherCancelReason = (reasonInp?.value || '').trim();
        saveState();
        showToast(slot.isWeatherCancelled ? 'Aula suspensa por tempo ruim' : 'Aula reativada com sucesso!');
        closeModal();
        renderApp();
      }
    });
  }

  // Add Custom Slot
  if (modalType === 'add-slot') {
    const btn = document.getElementById('btn-add-slot-submit');
    const timeInp = document.getElementById('new-slot-time');
    const periodInp = document.getElementById('new-slot-period');
    const instInp = document.getElementById('new-slot-instructor');

    btn?.addEventListener('click', () => {
      const time = (timeInp?.value || '').trim();
      if (!time) {
        alert('Informe o horário da aula.');
        return;
      }
      const period = (periodInp?.value || 'Tarde').trim();
      const instructor = (instInp?.value || 'Instrutor').trim();

      const newId = Date.now();
      state.slots.push({
        id: newId,
        time: time,
        period: period,
        instructor: instructor,
        maxSpots: 4,
        isWeatherCancelled: false,
        weatherCancelReason: '',
        isClosedClass: false,
        closedClassName: '',
        date: state.displayDate,
        orderIndex: state.slots.length + 1
      });

      saveState();
      showToast(`Horário das ${time} criado com sucesso!`);
      closeModal();
      renderApp();
    });
  }
}

// Fast Text Import Logic
function handleFastImport(text) {
  const lines = text.split('\n').map(l => l.trim()).filter(l => l.length > 0);
  if (lines.length < 2) {
    alert('Texto muito curto ou formato inválido.');
    return;
  }

  // Extract Date (Line 2 or first date pattern)
  let extractedDate = '';
  const datePattern = /(\d{1,2}[\/\-\.]\d{1,2}(?:[\/\-\.]\d{2,4})?)/;
  for (let l of lines) {
    const match = l.match(datePattern);
    if (match) {
      extractedDate = match[1];
      break;
    }
  }

  if (extractedDate) {
    state.displayDate = extractedDate;
  }

  // Extract Instructor
  let instructor = 'Instrutor Edvan';
  const instLine = lines.find(l => l.includes('📍') || l.toLowerCase().includes('professor'));
  if (instLine) {
    instructor = instLine.replace(/📍/g, '').replace(/professor/i, 'Instrutor').trim();
  }

  // Extract Slots
  const newSlots = [];
  let orderIndex = 1;

  for (let line of lines) {
    if (line.includes('🏄') || line.toLowerCase().includes(' as ') || line.toLowerCase().includes(' às ')) {
      // Clean emojis and find first time representation
      const cleaned = line.replace(/[^\d:apmAPM\s]/g, ' ').trim();
      const timeMatches = cleaned.match(/\d{1,2}:\d{2}/g);
      if (timeMatches && timeMatches.length > 0) {
        let startTime = timeMatches[0];
        if (startTime.length === 4) startTime = '0' + startTime; // Format 6:30 -> 06:30

        let period = 'Manhã';
        const hour = parseInt(startTime.split(':')[0], 10);
        if (hour < 8) period = 'Primeira Caída';
        else if (hour < 12) period = 'Manhã';
        else if (hour < 15) period = 'Meio-Dia';
        else if (hour < 18) period = 'Tarde';
        else period = 'Sunset';

        newSlots.push({
          id: Date.now() + orderIndex,
          time: startTime,
          period: period,
          instructor: instructor,
          maxSpots: 4,
          isWeatherCancelled: false,
          weatherCancelReason: '',
          isClosedClass: false,
          closedClassName: '',
          date: state.displayDate,
          orderIndex: orderIndex++
        });
      }
    }
  }

  if (newSlots.length === 0) {
    alert('Nenhum horário no formato "🏄 6:30 as 7:30" foi encontrado.');
    return;
  }

  // "Substituir tudo" (clear previous slots & bookings for the day)
  state.slots = newSlots;
  state.bookings = []; // Cleared so new day starts fresh
  saveState();

  showToast(`${newSlots.length} horários importados para ${state.displayDate}!`);
  renderApp();
}

// Toast Notification
function showToast(message) {
  const toast = document.getElementById('toast');
  if (!toast) return;

  toast.textContent = message;
  toast.classList.add('show');

  setTimeout(() => {
    toast.classList.remove('show');
  }, 3200);
}

// Security Escape Helper
function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

// Register PWA Service Worker
function registerServiceWorker() {
  if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
      navigator.serviceWorker.register('/sw.js')
        .then((reg) => {
          console.log('Onda Surf Service Worker registrado:', reg.scope);
        })
        .catch((err) => {
          console.warn('Falha ao registrar Service Worker:', err);
        });
    });
  }
}

// Start App on DOM Ready
document.addEventListener('DOMContentLoaded', initApp);
