package it.infocert.eigor.converter.fattpa2cen;

import com.google.common.base.Preconditions;
import it.infocert.eigor.api.ToCenConversion;
import it.infocert.eigor.converter.fattpa2cen.mapping.probablyDeprecated.FattPA2CenMapper;
import it.infocert.eigor.converter.fattpa2cen.models.FatturaElettronicaType;
import it.infocert.eigor.model.core.enums.Untdid5305DutyTaxFeeCategories;
import it.infocert.eigor.model.core.model.BG0000Invoice;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class FattPA2CenConverter implements ToCenConversion {

    public BG0000Invoice convert(InputStream input) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            doc = dBuilder.parse(input);
        } catch ( IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        assert doc != null;
        doc.getDocumentElement().normalize();



    }

    public BG0000Invoice convert(String fileName) {
        return convert(new File(fileName));
    }

    public BG0000Invoice convert(File file) {
        BG0000Invoice converted = null;
        try {
             converted = convert(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        return converted;
    }


    @Override
    public boolean support(String format) {
        return false;
    }
}
