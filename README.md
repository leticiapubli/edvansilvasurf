# Onda Surf - Aplicação Web & PWA

Aplicação Web moderna, responsiva e pronta para produção para agendamento de aulas de surf da **Edvan Silva Surf School**.

## 🚀 Como Hospedar na Vercel

### Opção 1: Pelo painel web da Vercel (Recomendado)
1. Conecte este repositório à sua conta na [Vercel](https://vercel.com).
2. Clique em **"Add New" > "Project"** e selecione o repositório.
3. Como o projeto é uma aplicação Web estática padrão com `vercel.json` e `package.json`, o framework preset será detectado automaticamente como **Other / Static**.
4. Clique em **Deploy**. Em menos de 1 minuto seu site estará online com HTTPS gratuito e CDN global!

### Opção 2: Pelo Terminal via Vercel CLI
```bash
npx vercel
```

---

## 💻 Como Rodar Localmente no Computador

Você pode rodar esta aplicação sem nenhuma dependência complexa:
```bash
# Com qualquer servidor web simples:
npx serve .
# Ou abrindo diretamente o arquivo index.html no seu navegador!
```

---

## 📱 Suporte a PWA (Progressive Web App)

- **Instalação no Celular / Computador:** O app possui `manifest.json` e `sw.js` (Service Worker) configurados.
  - No Chrome/Edge (Desktop): Clique no ícone de "Instalar aplicativo" na barra de navegação.
  - No iPhone (Safari): Toque em "Compartilhar" e depois em **"Adicionar à Tela de Início"**.
  - No Android (Chrome): Toque nos 3 pontinhos e em **"Instalar aplicativo"**.
- **Carregamento Instantâneo e Cache Offline:** Funciona mesmo com conexão instável na praia.

---

## 🏄 Funcionalidades Incluídas

1. **Inscrição Multi-Horários pelo Aluno:**
   - O mesmo aluno pode se inscrever em mais de uma aula no mesmo dia se houver vagas disponíveis.
   - Limite estrito de no máximo 4 alunos por horário.
2. **Painel do Aluno ("Suas Aulas Confirmadas"):**
   - No topo da tela, exibe todas as aulas agendadas pelo aluno com contador e botão de cancelamento individual para cada horário.
3. **Feedback Visual de Vagas:**
   - 🟢 Disponível (0-2 vagas ocupadas): tom azul-oceano/verde suave.
   - 🟠 Quase Lotado (3/4 alunos): contador e bordas em **Laranja** com aviso `• Resta 1 vaga!`.
   - 🔴 Lotado (4/4 alunos): contador e destaque em **Vermelho** com aviso `• Esgotado`.
   - 🟣 Aula Fechada: destaque em **Roxo** exclusivo com nome do aluno/particular.
4. **Painel do Instrutor / Admin (Discreto):**
   - Botão discreto **"Admin"** no topo superior direito protegido por login e senha (`admin` / `surf2026`).
   - **Importação Rápida de Horários:** Cole uma lista no formato:
     ```text
     Horario das aulas 
     01/11
     📍professor Edvan 
     🏄🏼♀️6:30 as 7:30
     🏄🏼♀️7:30 as 8:30
     🏄🏼♂️8:30 as 9:30
     ```
     O sistema lê a data automaticamente e cria os slots com o botão **"Substituir tudo"**.
   - Gerenciamento individual de alunos por horário (adicionar aluno manual, remover aluno).
   - "Marcar como Aula Fechada / Particular" com nome do aluno/grupo em fonte preta sobre fundo roxo.
   - Cancelamento de horários por tempo ruim / mar revolto com justificativa.
5. **Rodapé Oficial:**
   - Botões interativos com links diretos para o **Instagram da Escola** e **WhatsApp**.
   - Assinatura: *"Desenvolvido pela LeticiaPubli 2026"*.
