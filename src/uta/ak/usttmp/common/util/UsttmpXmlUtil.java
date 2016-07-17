/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uta.ak.usttmp.common.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author zhangcong
 */
public class UsttmpXmlUtil {
    
    private static final Logger logger=Logger.getLogger(UsttmpXmlUtil.class.getName());
    
    public static final String objectToXmlStr(Object obj, 
                                              Class cls,
                                              boolean removeXmlHead) throws JAXBException{
        
        JAXBContext context = JAXBContext.newInstance(cls);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, removeXmlHead);
//        CharacterEscapeHandler escapeHandler = new JaxbNoEscapeCharacterHandler();
//        m.setProperty("com.sun.xml.bind.characterEscapeHandler", escapeHandler); 
        m.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
        StringWriter sw = new StringWriter();
        m.marshal(obj,sw);
        String xmlstr=sw.toString();
//        String xmlstr=StringEscapeUtils.unescapeXml(sw.toString());
        logger.log(Level.FINE, "Converted to XML: {0}", xmlstr);
        
        return xmlstr;
    }
    
    public static final Object xmlStrToObject(String str, Class cls) throws JAXBException{
        
        JAXBContext crXontext = JAXBContext.newInstance(cls);
        Unmarshaller um =crXontext.createUnmarshaller();
        StringReader sr = new StringReader(str);
        logger.log(Level.FINE, "Converted to Object: {0}", str);
        return um.unmarshal(sr);
    }
    
}
