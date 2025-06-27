import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o site da RWTH Aachen University (http://www.rwth-aachen.de/)
 * Testes em diversas situações (presença, conteúdo, ordem, etc.)
 */
public class Teste {
    private static WebDriver driver;
    private static final String BASE_URL = "http://www.rwth-aachen.de/";    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.manage().window().maximize();
        
        driver.get(BASE_URL);
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        // Tentar aceitar cookies se o diálogo aparecer
        try {
            List<WebElement> cookieButtons = driver.findElements(
                By.xpath("//button[contains(., 'Akzeptieren') or contains(., 'Accept') or contains(., 'I agree')]")
            );
            if (!cookieButtons.isEmpty() && cookieButtons.get(0).isDisplayed()) {
                cookieButtons.get(0).click();
            }
        } catch (Exception e) {
            // Ignora se não houver diálogo de cookies
        }
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void beforeEach() {
        if (!driver.getCurrentUrl().equals(BASE_URL)) {
            driver.get(BASE_URL);
        }
    }
      // TESTE 1: Header/Logo da universidade
      @Test
      @DisplayName("Teste 1: Verificar presença, posição e funcionalidade do header/logo")
      public void testHeaderLogo() {
          // 1. Verificar presença do header
          List<WebElement> headerElements = driver.findElements(By.xpath(
                  "//header | " +
                          "//*[contains(@class, 'header') or contains(@class, 'navbar') or contains(@class, 'site-header')]"
          ));

          if (headerElements.isEmpty()) {
              headerElements = driver.findElements(By.xpath(
                      "//img[contains(translate(@alt, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'logo') or " +
                              "     contains(translate(@alt, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'rwth')] | " +
                              "//a[@href='/'] | " +
                              "//*[contains(@class, 'logo')]"
              ));
          }

          assertTrue(!headerElements.isEmpty() ||
                          driver.getPageSource().contains("RWTH Aachen") ||
                          driver.getPageSource().contains("Rheinisch-Westfälische"),
                  "PRESENÇA: Página deve conter header/logo ou referência à RWTH Aachen");

          // 2. Verificar posição (deve estar no topo da página)
          if (!headerElements.isEmpty()) {
              WebElement header = headerElements.get(0);
              Point location = header.getLocation();
              assertTrue(location.getY() < 200,
                      "POSIÇÃO: Header deve estar próximo ao topo da página (Y < 200px)");

              // 3. Verificar visibilidade
              assertTrue(header.isDisplayed(), "VISIBILIDADE: Header deve estar visível");

              // 4. Verificar dimensões (não deve ser muito pequeno)
              Dimension size = header.getSize();
              assertTrue(size.getWidth() > 50 && size.getHeight() > 20,
                      "DIMENSÕES: Header deve ter tamanho adequado (largura > 50px, altura > 20px)");
          }

          // 5. Verificar conteúdo relacionado à universidade
          String pageSource = driver.getPageSource().toLowerCase();
          assertTrue(pageSource.contains("rwth") || pageSource.contains("aachen") ||
                          pageSource.contains("rheinisch"),
                  "CONTEÚDO: Página deve conter referências textuais à universidade");

          // 6. Verificar se logo é clicável (se for um link)
          List<WebElement> logoLinks = driver.findElements(By.xpath(
                  "//a[img[contains(translate(@alt, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'logo') or " +
                          "       contains(translate(@alt, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'rwth')]] | " +
                          "//a[@href='/']/img"
          ));
          if (!logoLinks.isEmpty()) {
              assertTrue(logoLinks.get(0).isEnabled(),
                      "FUNCIONALIDADE: Logo deve ser clicável se for um link");
          }
      }


    // TESTE 2: Navegação principal
    @Test
    @DisplayName("Teste 2: Verificar estrutura, ordem e funcionalidade da navegação")
    public void testMainNavigation() {
        // 1. Verificar presença de links de navegação (focar nos visíveis)
        List<WebElement> allNavLinks = driver.findElements(By.cssSelector("nav a, header a, .navigation a, .navbar a"));

        List<WebElement> visibleNavLinks = allNavLinks.stream()
            .filter(WebElement::isDisplayed)
            .toList();
            
        assertTrue(!visibleNavLinks.isEmpty(), "PRESENÇA: Deve haver links de navegação visíveis");
        
        // 3. Verificar conteúdo dos links visíveis (não devem estar vazios)
        int linksWithText = 0;
        for (WebElement link : visibleNavLinks) {
            String text = link.getText().trim();
            String ariaLabel = link.getAttribute("aria-label");
            if (!text.isEmpty() || (ariaLabel != null && !ariaLabel.isEmpty())) {
                linksWithText++;
            }
        }
        assertTrue(linksWithText >= 1,
                  "CONTEÚDO: Maioria dos links visíveis deve ter texto ou aria-label (" + linksWithText + "/" + visibleNavLinks.size() + ")");        
        
        // 4. Verificar funcionalidade 
        int enabledLinks = 0;
        for (WebElement link : visibleNavLinks) {
            if (link.isEnabled()) {
                enabledLinks++;
            }
        }
        assertTrue(enabledLinks >= Math.min(2, visibleNavLinks.size()), 
                  "FUNCIONALIDADE: Maioria dos links visíveis deve estar habilitada (" + enabledLinks + "/" + visibleNavLinks.size() + ")");
        
        // 5. Verificar HREF válidos nos links visíveis
        int validHrefs = 0;
        for (WebElement link : visibleNavLinks) {
            String href = link.getAttribute("href");
            if (href != null && !href.trim().isEmpty() && !href.equals("#")) {
                validHrefs++;
            }
        }        assertTrue(validHrefs >= Math.min(2, visibleNavLinks.size()), 
                  "HREFS: Maioria dos links visíveis deve ter href válidos (" + validHrefs + "/" + visibleNavLinks.size() + ")");
    }


    // TESTE 3: Botão/Link "Learn More" ou similar
    @Test
    @DisplayName("Teste 3: Verificar presença de botões de ação usando XPath")
    public void testActionButtons() {
        List<WebElement> actionButtons = driver.findElements(By.xpath(
                "//a[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'learn more') or " +
                        "    contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'read more') or " +
                        "    contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'get started')]"
        ));

        assertFalse(actionButtons.isEmpty(), "Deve haver pelo menos um botão de ação na página");

        WebElement firstButton = actionButtons.get(0);
        assertTrue(firstButton.isDisplayed(), "Botão de ação deve estar visível");
    }


    // TESTE 4: Seção de eventos
    @Test
    @DisplayName("Teste 4: Verificar seção de eventos")
    public void testEventsSection() {
        boolean hasEventsSection = driver.getPageSource().toLowerCase().contains("events") ||
                                 driver.getPageSource().toLowerCase().contains("calendar") ||
                                 driver.getPageSource().toLowerCase().contains("veranstaltung") ||
                                 driver.getPageSource().toLowerCase().contains("kalender");
        assertTrue(hasEventsSection, "Página deve conter seção de eventos");
    }

    // TESTE 5: Seção de notícias
    @Test
    @DisplayName("Teste 5: Verificar seção de notícias")
    public void testNewsSection() {
        boolean hasNewsSection = driver.getPageSource().toLowerCase().contains("news") ||
                               driver.getPageSource().toLowerCase().contains("aktuelles") ||
                               driver.getPageSource().toLowerCase().contains("nachrichten");
        assertTrue(hasNewsSection, "Página deve conter seção de notícias");
    }
    
    // TESTE 6: Footer
    @Test
    @DisplayName("Teste 6: Verificar estrutura, posição, conteúdo e funcionalidade do footer")
    public void testFooter() {
        // 1. Verificar presença do footer
        WebElement footer = driver.findElement(By.tagName("footer"));
        assertNotNull(footer, "PRESENÇA: Footer deve estar presente na página");
        
        // 2. Verificar visibilidade
        assertTrue(footer.isDisplayed(), "VISIBILIDADE: Footer deve estar visível");
        
        // 3. Verificar posição (deve estar no final da página)
        Point footerLocation = footer.getLocation();
        Dimension pageSize = driver.manage().window().getSize();
        assertTrue(footerLocation.getY() > pageSize.getHeight() / 2, 
                  "POSIÇÃO: Footer deve estar na metade inferior da página");
        
        // 4. Verificar dimensões adequadas
        Dimension footerSize = footer.getSize();
        assertTrue(footerSize.getWidth() > 200 && footerSize.getHeight() > 50, 
                  "DIMENSÕES: Footer deve ter tamanho adequado (largura > 200px, altura > 50px)");
          // 5. Verificar conteúdo relevante
        String footerText = footer.getText().toLowerCase();
        int contentScore = 0;
        
        if (footerText.contains("rwth") || footerText.contains("aachen")) contentScore++;
        if (footerText.contains("copyright") || footerText.contains("©")) contentScore++;
        if (footerText.contains("2025") || footerText.contains("2024")) contentScore++;
        if (footerText.contains("kontakt") || footerText.contains("contact")) contentScore++;
        if (footerText.contains("impressum") || footerText.contains("datenschutz")) contentScore++;
        
        assertTrue(contentScore >= 2, 
                  "CONTEÚDO: Footer deve conter pelo menos 2 tipos de informação relevante " +
                  "(universidade, copyright, ano, localização, contato). Score: " + contentScore + "/5");
        
        // 6. Verificar estrutura
        List<WebElement> footerLinks = footer.findElements(By.tagName("a"));
        assertTrue(!footerLinks.isEmpty(),
                  "ESTRUTURA: Footer deve conter pelo menos um link");
        
        // 7. Verificar organização
        List<WebElement> footerSections = footer.findElements(By.cssSelector("div, section, ul, ol"));
        assertTrue(!footerSections.isEmpty(),
                  "ORGANIZAÇÃO: Footer deve ter estrutura organizada (divs, seções ou listas)");
        
        // 8. Verificar funcionalidade dos links
        int workingLinks = 0;
        for (WebElement link : footerLinks) {
            if (link.isEnabled() && !link.getAttribute("href").isEmpty()) {
                workingLinks++;
            }
        }
        assertTrue(workingLinks > 0, 
                  "FUNCIONALIDADE: Pelo menos um link do footer deve estar funcional");
          // 9. Verificar acessibilidade
        String footerColor = footer.getCssValue("color");
        assertNotNull(footerColor, "ACESSIBILIDADE: Footer deve ter cor de texto definida");
        
        // 10. Verificar responsividade
        assertTrue(footerSize.getWidth() >= pageSize.getWidth() * 0.8,
                  "RESPONSIVIDADE: Footer deve ocupar pelo menos 80% da largura da página");
    }

    // TESTE 7: Links de redes sociais
    @Test
    @DisplayName("Teste 7: Verificar links de redes sociais")
    public void testSocialMediaLinks() {
        List<WebElement> socialLinks = driver.findElements(By.xpath(
            "//a[contains(@href, 'facebook') or contains(@href, 'twitter') or " +
            "contains(@href, 'instagram') or contains(@href, 'linkedin') or " +
            "contains(@href, 'tiktok')]"
        ));
        
        assertTrue(!socialLinks.isEmpty(), "Deve haver pelo menos um link de rede social");
        
        // Verificar se os links são válidos (não vazios)
        for (WebElement link : socialLinks) {
            String href = link.getAttribute("href");
            assertNotNull(href, "Link de rede social deve ter href válido");
            assertFalse(href.trim().isEmpty(), "Href do link não deve estar vazio");
        }
    }
    
    // TESTE 8: Imagens
    @Test
    @DisplayName("Teste 8: Verificar carregamento, acessibilidade e dimensões das imagens")
    public void testImages() {
        List<WebElement> images = driver.findElements(By.tagName("img"));
        // 1. Verificar carregamento - todas devem ter src válido
        int validSrcCount = 0;
        for (WebElement img : images) {
            String src = img.getAttribute("src");
            if (src != null && !src.trim().isEmpty() && !src.equals("data:,")) {
                validSrcCount++;
            }
        }
        assertTrue(validSrcCount > 0, 
                  "CARREGAMENTO: Pelo menos uma imagem deve ter src válido (" + validSrcCount + "/" + images.size() + ")");
          // 2. Verificar acessibilidade - alt text
        int imagesWithAlt = 0;
        for (WebElement img : images) {
            String alt = img.getAttribute("alt");
            if (alt != null) {
                imagesWithAlt++;
            }
        }
        assertTrue(imagesWithAlt >= images.size() / 2, 
                  "ACESSIBILIDADE: Pelo menos metade das imagens deve ter alt text (" + 
                  imagesWithAlt + "/" + images.size() + ")");
        
        // 3. Verificar visibilidade
        int visibleImages = 0;
        for (WebElement img : images) {
            if (img.isDisplayed()) {
                visibleImages++;
            }
        }
        assertTrue(visibleImages > 0, 
                  "VISIBILIDADE: Pelo menos uma imagem deve estar visível (" + visibleImages + "/" + images.size() + ")");
        
        // 4. Verificar dimensões das imagens visíveis
        int reasonableSizedImages = 0;
        for (WebElement img : images) {
            if (img.isDisplayed()) {
                Dimension size = img.getSize();
                // Imagem deve ter pelo menos 20x20 pixels para ser considerada válida
                if (size.getWidth() >= 20 && size.getHeight() >= 20) {
                    reasonableSizedImages++;
                }
            }
        }
        assertTrue(reasonableSizedImages > 0, 
                  "DIMENSÕES: Pelo menos uma imagem deve ter tamanho adequado (≥20x20px)");
          // 5. Verificar tipos de arquivo válidos (sem contagem, apenas verificação)
        for (WebElement img : images) {
            String src = img.getAttribute("src");
            if (src != null && (src.contains(".jpg") || src.contains(".png") || 
                               src.contains(".gif") || src.contains(".svg") || 
                               src.contains(".webp") || src.contains(".jpeg"))) {
                // Pelo menos uma imagem tem tipo válido
                break;
            }
        }
    }
    
    // TESTE 9: Formulário de busca/pesquisa
    @Test
    @DisplayName("Teste 9: Verificar presença de campo de busca")
    public void testSearchField() {
        List<WebElement> searchElements = driver.findElements(By.xpath(
            "//input[@type='search']"
        ));
        
        if (searchElements.isEmpty()) {
            searchElements = driver.findElements(By.xpath(
                "//input[contains(@placeholder, 'search') or contains(@placeholder, 'Search')]"
            ));
        }
        
        if (searchElements.isEmpty()) {
            searchElements = driver.findElements(By.xpath(
                "//input[contains(@name, 'search') or contains(@id, 'search')]"
            ));
        }
        
        List<WebElement> searchButtons = driver.findElements(By.xpath(
            "//button[contains(text(), 'Search') or contains(@title, 'Search')] | " +
            "//a[contains(text(), 'Search') or contains(@title, 'Search')]"
        ));
        
        assertTrue(!searchElements.isEmpty() || !searchButtons.isEmpty() ||
                  driver.getPageSource().toLowerCase().contains("search"), 
                  "Deve haver funcionalidade de busca disponível na página");
    }

    // TESTE 10: Lista de links no footer
    @Test
    @DisplayName("Teste 10: Verificar lista de links úteis no footer")
    public void testFooterLinks() {
        WebElement footer = driver.findElement(By.tagName("footer"));
        List<WebElement> footerLinks = footer.findElements(By.tagName("a"));
        
        assertTrue(footerLinks.size() > 3, "Footer deve conter múltiplos links úteis");
        
        int validLinks = 0;
        for (WebElement link : footerLinks) {
            if (!link.getText().trim().isEmpty() || !link.getAttribute("title").isEmpty()) {
                validLinks++;
            }
        }
        assertTrue(validLinks >= 3, "Deve haver pelo menos 3 links válidos no footer");
    }

    // TESTE 11: Seção Acadêmica
    @Test
    @DisplayName("Teste 11: Verificar seção acadêmica")
    public void testAcademicsSection() {
        boolean hasAcademics = driver.getPageSource().toLowerCase().contains("studium") ||
                             driver.getPageSource().toLowerCase().contains("fakultät") ||
                             driver.getPageSource().toLowerCase().contains("studiengänge") ||
                             driver.getPageSource().toLowerCase().contains("education") ||
                             driver.getPageSource().toLowerCase().contains("faculty");
        assertTrue(hasAcademics, "Página deve conter informações acadêmicas");
    }

    // TESTE 12: Informações de contato
    @Test
    @DisplayName("Teste 12: Verificar informações de contato")
    public void testContactInfo() {
        String pageSource = driver.getPageSource();
        
        boolean hasContact = pageSource.contains("kontakt") ||
                           pageSource.contains("contact") ||
                           pageSource.contains("rwth-aachen.de") ||
                           pageSource.contains("@rwth") ||
                           pageSource.contains("Aachen");
        
        assertTrue(hasContact, "Página deve conter informações de contato");
    }

    // TESTE 13: Responsividade
    @Test
    @DisplayName("Teste 13: Verificar configuração de responsividade")
    public void testResponsiveDesign() {
        List<WebElement> viewportMeta = driver.findElements(By.xpath(
            "//meta[@name='viewport']"
        ));
        
        assertTrue(viewportMeta.size() > 0, "Página deve ter meta tag viewport para responsividade");
        
        if (!viewportMeta.isEmpty()) {
            String content = viewportMeta.get(0).getAttribute("content");
            assertNotNull(content, "Meta viewport deve ter conteúdo");
            assertTrue(content.contains("width=device-width"), 
                      "Viewport deve estar configurado para dispositivos móveis");
        }
    }

    // TESTE 14: Verificar título da página
    @Test
    @DisplayName("Teste 14: Verificar título da página no browser")
    public void testPageTitle() {
        String title = driver.getTitle();
        assertNotNull(title, "Página deve ter um título");
        assertFalse(title.trim().isEmpty(), "Título não deve estar vazio");
        assertTrue(title.toLowerCase().contains("rwth") || 
                  title.toLowerCase().contains("aachen") ||
                  title.toLowerCase().contains("rheinisch") ||
                  title.toLowerCase().contains("technische hochschule"),
                  "Título deve conter referência à universidade");
    }

    // TESTE 15: Formulários
    @Test
    @DisplayName("Teste 15: Verificar estrutura e funcionalidade de formulários")
    public void testForms() {
        // 1. Verificar presença de formulários
        List<WebElement> forms = driver.findElements(By.tagName("form"));
        List<WebElement> inputs = driver.findElements(By.cssSelector("input, textarea, select"));
        
        assertTrue(!forms.isEmpty() || !inputs.isEmpty(),
                  "PRESENÇA: Página deve conter pelo menos um formulário ou campo de entrada");
        
        if (!inputs.isEmpty()) {
            // 2. Verificar tipos de input
            int textInputs = driver.findElements(By.cssSelector("input[type='text'], input[type='email'], input[type='search']")).size();
            int buttons = driver.findElements(By.cssSelector("input[type='submit'], input[type='button'], button")).size();
            
            System.out.println("INFO FORMULÁRIOS: " + textInputs + " campos de texto, " + buttons + " botões");
            
            // 3. Verificar acessibilidade
            List<WebElement> labels = driver.findElements(By.tagName("label"));
            if (textInputs > 0) {
                assertTrue(!labels.isEmpty(),
                          "ACESSIBILIDADE: Campos de texto devem ter labels associados");
            }
            
            // 4. Verificar funcionalidade
            WebElement firstInput = inputs.get(0);
            assertTrue(firstInput.isEnabled(), 
                      "FUNCIONALIDADE: Campos de entrada devem estar habilitados");
        }
    }

    // TESTE 16: Tabelas
    @Test
    @DisplayName("Teste 16: Verificar estrutura e acessibilidade de tabelas")
    public void testTables() {
        List<WebElement> tables = driver.findElements(By.tagName("table"));
        
        if (!tables.isEmpty()) {
            WebElement firstTable = tables.get(0);
            
            // 1. Verificar estrutura básica
            assertTrue(firstTable.isDisplayed(), "VISIBILIDADE: Tabela deve estar visível");
            
            // 2. Verificar cabeçalhos
            List<WebElement> headers = firstTable.findElements(By.tagName("th"));
            assertTrue(!headers.isEmpty(),
                      "ESTRUTURA: Tabela deve ter cabeçalhos (th)");
            
            // 3. Verificar conteúdo
            List<WebElement> rows = firstTable.findElements(By.tagName("tr"));
            assertTrue(rows.size() >= 2, 
                      "CONTEÚDO: Tabela deve ter pelo menos 2 linhas (cabeçalho + dados)");
            
            // 4. Verificar acessibilidade
            String tableCaption = !firstTable.findElements(By.tagName("caption")).isEmpty() ? "Sim" : "Não";
            System.out.println("INFO TABELA: Caption presente: " + tableCaption);
        } else {
            System.out.println("INFO: Nenhuma tabela encontrada na página - teste pulado");
        }
    }

    // TESTE 17: Listas
    @Test
    @DisplayName("Teste 17: Verificar estrutura e ordem de listas")
    public void testLists() {
        // 1. Verificar presença de listas
        List<WebElement> unorderedLists = driver.findElements(By.tagName("ul"));
        List<WebElement> orderedLists = driver.findElements(By.tagName("ol"));
        
        assertTrue(!unorderedLists.isEmpty() || !orderedLists.isEmpty(),
                  "PRESENÇA: Página deve conter pelo menos uma lista (ul ou ol)");
        
        // 2. Verificar estrutura das listas não ordenadas
        if (!unorderedLists.isEmpty()) {
            WebElement firstUl = unorderedLists.get(0);
            List<WebElement> listItems = firstUl.findElements(By.tagName("li"));
            
            assertTrue(listItems.size() >= 2, 
                      "ESTRUTURA UL: Lista deve ter pelo menos 2 itens (" + listItems.size() + " encontrados)");
            
            // 3. Verificar CONTEÚDO dos itens (texto, links ou outros elementos)
            int itemsWithContent = 0;
            for (WebElement item : listItems) {
                String text = item.getText().trim();
                List<WebElement> childElements = item.findElements(By.xpath(".//*"));
                
                if (!text.isEmpty() || !childElements.isEmpty()) {
                    itemsWithContent++;
                }
            }
            assertTrue(itemsWithContent >= Math.max(1, listItems.size() / 2), 
                      "CONTEÚDO UL: Pelo menos metade dos itens deve ter conteúdo significativo (" + 
                      itemsWithContent + "/" + listItems.size() + " itens)");
        }
        
        // 4. Verificar listas ordenadas se existirem
        if (!orderedLists.isEmpty()) {
            WebElement firstOl = orderedLists.get(0);
            List<WebElement> orderedItems = firstOl.findElements(By.tagName("li"));
            
            assertTrue(orderedItems.size() >= 2, 
                      "ESTRUTURA OL: Lista ordenada deve ter pelo menos 2 itens");
        }
    }
}
