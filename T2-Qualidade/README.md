# Testes Selenium para RWTH Aachen University

Este projeto contém testes automatizados para o site da RWTH Aachen University (http://www.rwth-aachen.de) utilizando Selenium WebDriver.

## Pré-requisitos

1. **Java 17+** instalado
2. **Maven** instalado
3. **ChromeDriver** no PATH

## Descrição

Este projeto implementa 17 testes automatizados para validar diferentes aspectos do site da RWTH Aachen University:

- Testes de Interface (logo, navegação, responsividade)
- Testes de Conteúdo (título, seções, links)
- Testes de Funcionalidade (busca, formulários, idioma)
- Testes de Acessibilidade (alt text, labels, meta tags)
- Testes de Estrutura (tabelas, listas, imagens)

Os testes são executados usando Selenium WebDriver em Java com o framework JUnit 5.

## Como Executar

Execute o comando:
```bash
mvn test
```

## Estrutura do Projeto

```
T2-Qualidade/
├── pom.xml                    # Configuração Maven
├── README.md                  # Este arquivo
└── src/
    └── test/
        └── java/
            └── Teste.java     # 17 testes automatizados
```

## Testes Implementados

1. **Teste 1:** Verificar presença do logo/header
2. **Teste 2:** Verificar navegação principal
3. **Teste 3:** Verificar campo de busca
4. **Teste 4:** Verificar título da página
5. **Teste 5:** Verificar presença do footer
6. **Teste 6:** Verificar links de redes sociais
7. **Teste 7:** Verificar design responsivo
8. **Teste 8:** Verificar imagens e acessibilidade
9. **Teste 9:** Verificar links para faculdades/departamentos
10. **Teste 10:** Verificar seção de notícias/eventos
11. **Teste 11:** Verificar seção de pesquisa/investigação
12. **Teste 12:** Verificar informações para estudantes
13. **Teste 13:** Verificar seletor de idioma
14. **Teste 14:** Verificar links do rodapé
15. **Teste 15:** Verificar meta tags e acessibilidade
16. **Teste 16:** Verificar tabelas (estrutura e acessibilidade)
17. **Teste 17:** Verificar listas (estrutura e organização)

## Tecnologias Utilizadas

- **Java 17**
- **Maven** - Gerenciamento de dependências
- **Selenium WebDriver 4.21.0** - Automação web
- **JUnit 5** - Framework de testes
- **ChromeDriver** - Navegador para testes

## Aspectos Testados

### Interface e Usabilidade
- Presença e posicionamento de elementos
- Responsividade da página
- Funcionalidade de navegação

### Acessibilidade
- Alt text em imagens
- Labels em formulários
- Meta tags apropriadas
- Estrutura semântica HTML

### Conteúdo e Funcionalidade
- Links funcionais
- Campos de busca
- Informações institucionais
- Seletor de idioma

## Solução de Problemas

1. **Erro de driver não encontrado**: Verifique se ChromeDriver está no PATH
2. **TimeoutException**: O site pode estar lento, verifique sua conexão de internet
3. **Elemento não encontrado**: O site pode ter mudado, os testes podem precisar de atualização
4. **Erro de Java**: Certifique-se de que Java 17+ está instalado e configurado

## Autor

Projeto desenvolvido para testes de qualidade de software da RWTH Aachen University.
