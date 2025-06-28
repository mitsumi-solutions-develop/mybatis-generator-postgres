package io.github.mitsumi.solutions.mybatis.generator.postgres.config.parsers;

import lombok.SneakyThrows;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.config.xml.ParserEntityResolver;
import org.mybatis.generator.config.xml.ParserErrorHandler;
import org.mybatis.generator.exception.XMLParserException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

@SuppressWarnings({"PMD.LocalVariableCouldBeFinal", "PMD.UseExplicitTypes", "PMD.CommentRequired"})
public class PostgresConfigurationParser extends ConfigurationParser {
    private final Properties extraProperties;

    public PostgresConfigurationParser(final Properties extraProperties, final List<String> warnings) {
        super(extraProperties, warnings);
        this.extraProperties = extraProperties;
    }

    @Override
    public Configuration parseConfiguration(final Reader reader) {

        final InputSource inputSource = new InputSource(reader);

        return parseConfiguration(inputSource);
    }

    @SneakyThrows
    private Configuration parseConfiguration(final InputSource inputSource) {

        final Document document = document(inputSource);

        final Element rootNode = document.getDocumentElement();
        final DocumentType docType = document.getDoctype();
        if (rootNode.getNodeType() == Node.ELEMENT_NODE &&
            XmlConstants.MYBATIS_GENERATOR_CONFIG_PUBLIC_ID.equals(docType.getPublicId())) {

            return parseMyBatisGeneratorConfiguration(rootNode);
        } else {
            throw new IllegalStateException(getString("RuntimeError.5"));
        }
    }

    @SneakyThrows
    private Document document(final InputSource inputSource) {

        return builder().parse(inputSource);
    }

    @SneakyThrows
    private DocumentBuilder builder() {
        final var handler = new ParserErrorHandler(new ArrayList<>(), new ArrayList<>());

        var builder = factory().newDocumentBuilder();

        builder.setEntityResolver(new ParserEntityResolver());

        builder.setErrorHandler(handler);

        return builder;
    }

    @SneakyThrows
    private DocumentBuilderFactory factory() {
        var factory = DocumentBuilderFactory.newInstance();

        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setValidating(true);

        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        return factory;
    }

    private Configuration parseMyBatisGeneratorConfiguration(final Element rootNode)
        throws XMLParserException {
        var parser = new MybatisGeneratorPostgresConfigurationParser(extraProperties);

        return parser.parseConfiguration(rootNode);
    }
}
