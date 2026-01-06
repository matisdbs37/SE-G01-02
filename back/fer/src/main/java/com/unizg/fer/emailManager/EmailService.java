package com.unizg.fer.emailManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private final ObjectMapper mapper;

    private static final String TEMPLATE_FOLDER = "./back/fer/src/main/resources/email_templates/";

    private static final String KEY_NOT_FOUND_EXCEPTION = "The key %s was not found in this JSON template %s ";

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public EmailService() {
        this.mapper = new ObjectMapper();
    }

    /***
     * Update the the variables present in the key of the json template with
     * provided strings
     * 
     * @See resources/email_template for further informations on @param templateName
     *      key/values
     * @See JSONKeys.java
     * @See JSONValues.java
     * @param templateName the name of the template to use to create the email
     * @param keys         List of all the JSON keys to update
     * @param values       Map(s) of key/values a key is the name of the JSON key
     *                     for example
     * @return EmailTemplate with the desired values
     * @throws IOException
     */
    @SuppressWarnings("unused")
    private EmailTemplate editTemplateKey(String templateName, List<JSONKeys> keys, Map<JSONValues, String> values)
            throws IOException {
        // load the template
        ObjectNode template = (ObjectNode) mapper.readTree(new File(TEMPLATE_FOLDER + templateName));
        // itearte on all the keys provided
        for (var key : keys) {
            var node = template.get(key.getValue());
            // check JSON node validity
            if (node == null || node.isNull()) {
                throw new IllegalArgumentException(
                        String.format(KEY_NOT_FOUND_EXCEPTION, key.getValue(), templateName));
            }
            var textNode = node.asText();
            // iterate over values
            for (Map.Entry<JSONValues, String> entry : values.entrySet()) {
                // get the first place holder of the node
                String placeholder = "{" + entry.getKey().getValue() + "}";
                String newValue = entry.getValue();
                // remplace the previous {JSONValue} by it's value
                textNode = textNode.replace(placeholder, newValue);
            }
            // update the node value
            template.put(key.getValue(), textNode);
        }
        // convert into email template
        EmailTemplate result = mapper.treeToValue(template, EmailTemplate.class);
        return result;
    }

    /**
     * M
     * 
     * @param userEmail
     * @param type      TemplateType to edit
     * @param values    
     */
    public void sendEmail(String recipient, TemplateType type, Map<JSONValues, String> values) {
        try {
            // 1. Charger et éditer le template via la fonction privée
            // On traite systématiquement SUBJECT et BODY
            EmailTemplate template = editTemplateKey(
                    type.getFileName(),
                    List.of(JSONKeys.SUBJECT, JSONKeys.BODY),
                    values);

            // 2. Préparer le message Mime (support HTML)
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(template.getFrom());
            helper.setTo(recipient);
            helper.setSubject(template.getSubject());
            helper.setText(template.getBody(), template.isHtml());

            // 3. Envoi
            mailSender.send(message);
            LOGGER.info("Email '{}' envoyé avec succès à {}", type, recipient);

        } catch (Exception e) {
            LOGGER.error("Erreur lors de l'envoi de l'email {} à {}", type, recipient, e);
            throw new RuntimeException("Échec de l'envoi de l'email", e);
        }
    }
}
