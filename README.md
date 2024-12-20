# EcoTrack - Monitoramento de Consumo de Energia

## Integrantes
- Gustavo Maia (RM: 553270)
- Rafael Vida (RM: 553721)
- Kauã Silveira (RM: 552618)

**Turma:** 2TDSPS

## Links do Projeto
- [Design no Figma](https://www.figma.com/design/e6EhlV2G9Rt92rlRbt7oaP/EcoTrack?node-id=0-1&t=QF1BAKjQv9JaT2wc-1)
- [Vídeo Demonstrativo](https://youtu.be/ZT_yaunO9J8)
- [Repositório da API](https://github.com/GlobalEcoTrack/javaAdvanced)

## Sobre o Projeto

O EcoTrack é uma aplicação mobile desenvolvida em Kotlin para Android que tem como objetivo ajudar os usuários a monitorar e gerenciar seu consumo de energia elétrica. Com o aumento da preocupação com sustentabilidade e custos de energia, nossa aplicação oferece uma maneira intuitiva de acompanhar o consumo de diferentes eletrodomésticos.

## Arquitetura do Sistema

O projeto é dividido em duas partes principais:

### 1. Frontend (Android)
Este repositório contém a aplicação mobile desenvolvida em Kotlin para Android, responsável pela interface do usuário e interação com o sistema.

### 2. Backend (Java Spring)
A API REST do projeto está disponível em um [repositório separado](https://github.com/GlobalEcoTrack/javaAdvanced) e foi desenvolvida utilizando:
- Java 17
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- ORACLESQL
- Maven

## Funcionalidades Principais

### 1. Autenticação e Cadastro
- Sistema de login e cadastro de usuários
- Cadastro com informações pessoais e localização (estado) para cálculos precisos de tarifa

### 2. Dashboard Principal
- Medidor visual de consumo total
- Cards de acesso rápido para diferentes funcionalidades
- Visualização intuitiva do consumo atual

### 3. Gerenciamento de Eletrodomésticos
- Cadastro de eletrodomésticos com:
  - Tempo de uso diário (em minutos)
  - Frequência semanal de uso
  - Cálculo automático de consumo (kWh)
- Suporte para múltiplos eletrodomésticos do mesmo tipo
- Exclusão de eletrodomésticos cadastrados

### 4. Monitoramento de Consumo
- Visualização detalhada do consumo por eletrodoméstico
- Cálculo do custo baseado na tarifa regional
- Agrupamento de eletrodomésticos por categoria
- Relatórios detalhados de consumo

### 5. Relatórios Mensais
- Acompanhamento histórico do consumo
- Análise de custos mensais
- Cálculo de emissão de CO2

## Tecnologias Utilizadas

### Frontend (Android)
- Kotlin
- Android SDK
- Retrofit para comunicação com API
- Coroutines para operações assíncronas
- ViewBinding para manipulação de views
- Material Design para interface

### Backend (API)
- Java 17
- Spring Boot
- Spring Security com JWT
- Spring Data JPA
- ORACLESQL
- Maven

### Arquitetura e Padrões
- MVVM (Model-View-ViewModel)
- Clean Architecture
- Repository Pattern
- Singleton Pattern para gerenciamento de instâncias

### Bibliotecas Principais
- Retrofit2: Comunicação com API REST
- OkHttp3: Cliente HTTP e interceptação
- Gson: Serialização/deserialização JSON
- Lifecycle Components: Gerenciamento do ciclo de vida
- Material Design Components: Interface do usuário

## Estrutura do Projeto

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/ecotrack/
│   │   │   ├── data/
│   │   │   │   ├── model/          # Classes de dados
│   │   │   │   └── remote/         # Configuração Retrofit e APIs
│   │   │   ├── interfaces/         # Interfaces do sistema
│   │   │   ├── view/              # Componentes customizados de UI
│   │   │   └── activities/        # Activities do Android
│   │   └── res/                   # Recursos (layouts, drawables, etc)
│   └── test/                      # Testes unitários
└── build.gradle                   # Configurações do projeto
```

## Endpoints da API

A API fornece os seguintes endpoints principais:

### Autenticação
- POST `/auth/signup` - Cadastro de usuário
- POST `/auth/login` - Login de usuário

### Eletrodomésticos
- GET `/appliance` - Lista todos os eletrodomésticos disponíveis
- POST `/userAppliance` - Cadastra um eletrodoméstico para o usuário
- DELETE `/userAppliance/{id}` - Remove um eletrodoméstico do usuário
- GET `/userAppliance/report` - Relatório de consumo geral
- GET `/userAppliance/report/monthYear` - Relatório mensal
- GET `/userAppliance/user/appliance/{id}` - Lista eletrodomésticos por tipo

### Estados
- GET `/state` - Lista todos os estados com suas tarifas

## Funcionalidades Detalhadas

### Cadastro de Usuário
- Nome completo
- Data de nascimento
- Estado (para cálculo da tarifa regional)
- E-mail
- Senha
- Validações de campos e formato

### Cadastro de Eletrodomésticos
- Seleção do tipo de eletrodoméstico
- Configuração de tempo de uso
- Validações de limite de tempo
- Cálculo automático de consumo

### Visualização de Consumo
- Cards interativos por eletrodoméstico
- Detalhamento de consumo individual
- Opções de gerenciamento
- Atualização em tempo real

### Relatórios
- Consumo mensal em kWh
- Custo mensal em reais
- Impacto ambiental (CO2)
- Histórico de consumo

## Configuração do Ambiente

### Backend (API)
1. Clone o repositório da API:
```bash
git clone https://github.com/GlobalEcoTrack/javaAdvanced.git
```

2. Configure o banco de dados PostgreSQL no `application.properties`

3. Execute a aplicação:
```bash
mvn spring-boot:run
```

### Frontend (Android)
1. Clone o repositório
2. Abra o projeto no Android Studio
3. Configure o SDK Android (mínimo API 33)
4. Sincronize o projeto com os arquivos Gradle
5. Execute em um emulador ou dispositivo físico

