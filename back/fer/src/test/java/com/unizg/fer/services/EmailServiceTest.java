package com.unizg.fer.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unizg.fer.emailManager.EmailService;
import com.unizg.fer.emailManager.EmailTemplate;
import com.unizg.fer.emailManager.JSONValues;
import com.unizg.fer.emailManager.TemplateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    // On mock l'ObjectMapper car on ne veut pas lire de vrais fichiers pendant les tests
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EmailService emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        // Initialisation d'un MimeMessage vide pour les mocks
        mimeMessage = new MimeMessage((Session) null);

        // ASTUCE : Comme le mapper est instancié avec 'new ObjectMapper()' dans le constructeur,
        // @InjectMocks ne peut pas l'écraser automatiquement. On utilise ReflectionTestUtils.
        ReflectionTestUtils.setField(emailService, "mapper", objectMapper);
    }

    @Test
    void sendEmail_Success_ShouldSendEmailWithReplacedValues() throws IOException {
        // --- ARRANGE ---
        String recipient = "test@example.com";
        String templateFilename = "welcome.json";

        // Mock des Enums (Adapte selon tes vrais Enums)
        TemplateType mockType = mock(TemplateType.class);
        when(mockType.getFileName()).thenReturn(templateFilename);

        Map<JSONValues, String> values = new HashMap<>();
        // Supposons que JSONValues soit une enum, on utilise un mock ou une vraie valeur si possible
        JSONValues valName = mock(JSONValues.class);
        when(valName.getValue()).thenReturn("USERNAME");
        values.put(valName, "Jean Dupont");

        // 1. Préparer le Mock du JSON (ObjectNode)
        ObjectNode mockJsonNode = JsonNodeFactory.instance.objectNode();
        // Le service attend SUBJECT et BODY (voir List.of dans sendEmail)
        // On met des placeholders pour tester le remplacement : {USERNAME}
        mockJsonNode.put("subject", "Bienvenue {USERNAME} !");
        mockJsonNode.put("body", "Bonjour {USERNAME}, merci de nous rejoindre.");

        // Mock de la lecture du fichier : quand on lit n'importe quel fichier, on retourne notre noeud JSON
        when(objectMapper.readTree(any(File.class))).thenReturn(mockJsonNode);

        // 2. Préparer le résultat de la conversion en objet EmailTemplate
        EmailTemplate mockTemplateObj = new EmailTemplate();
        mockTemplateObj.setFrom("noreply@fer.hr");
        mockTemplateObj.setSubject("Bienvenue Jean Dupont !"); // Valeur attendue après remplacement
        mockTemplateObj.setBody("Bonjour Jean Dupont, merci de nous rejoindre.");
        mockTemplateObj.setHtml(true);

        // Mock de la conversion treeToValue
        when(objectMapper.treeToValue(any(ObjectNode.class), eq(EmailTemplate.class)))
                .thenReturn(mockTemplateObj);

        // Mock de JavaMailSender
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // --- ACT ---
        // Note: JSONKeys.SUBJECT et BODY sont utilisés en interne, assure-toi que leurs .getValue() correspondent aux clés du mockJsonNode ("subject", "body")
        emailService.sendEmail(recipient, mockType, values);

        // --- ASSERT ---
        // Vérifier que le mail a été envoyé
        verify(mailSender).send(mimeMessage);

        // Vérification plus poussée : est-ce que le sujet et le corps ont bien été modifiés dans le JSON avant conversion ?
        // On vérifie que le noeud JSON a bien reçu les nouvelles valeurs (via put)
        assertEquals("Bienvenue Jean Dupont !", mockJsonNode.get("subject").asText());
        assertEquals("Bonjour Jean Dupont, merci de nous rejoindre.", mockJsonNode.get("body").asText());
    }

    @Test
    void sendEmail_KeyNotFound_ShouldThrowException() throws IOException {
        // --- ARRANGE ---
        TemplateType mockType = mock(TemplateType.class);
        when(mockType.getFileName()).thenReturn("error.json");

        // Le JSON retourné ne contient PAS la clé "body" (simule un template corrompu)
        ObjectNode mockJsonNode = JsonNodeFactory.instance.objectNode();
        mockJsonNode.put("subject", "Sujet Ok");
        // Pas de body

        when(objectMapper.readTree(any(File.class))).thenReturn(mockJsonNode);

        // --- ACT & ASSERT ---
        // On s'attend à une RuntimeException qui wrap l'IllegalArgumentException lancée dans editTemplateKey
        Exception exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendEmail("test@test.com", mockType, new HashMap<>());
        });

        // Vérifier la cause racine
        assertTrue(exception.getMessage().contains("Échec de l'envoi"));
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }
    /*
    @Test
    void sendEmail_MailSenderFails_ShouldLogAndThrow() throws IOException {
        TemplateType mockType = mock(TemplateType.class);
        when(mockType.getFileName()).thenReturn("dummy.json");

        ObjectNode mockJsonNode = JsonNodeFactory.instance.objectNode();
        mockJsonNode.put("subject", "S");
        mockJsonNode.put("body", "B");

        when(objectMapper.readTree(any(File.class))).thenReturn(mockJsonNode);
        when(objectMapper.treeToValue(any(), eq(EmailTemplate.class))).thenReturn(new EmailTemplate());

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new RuntimeException("SMTP Error")).when(mailSender).send(any(MimeMessage.class));

        assertThrows(RuntimeException.class, () -> {
            emailService.sendEmail("fail@test.com", mockType, new HashMap<>());
        });

        verify(mailSender).send(any(MimeMessage.class));
    }
     */
}