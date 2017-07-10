package com.vng.wpl.tools;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Created by steven on 10/07/2017.
 */
public class Main {

    private static void help() {
        System.out.println("Usage: parse [OPTION] SRC DEST");
        System.out.println("OPTIONS:");
        System.out.println("\t-o: from .xml file to .csv file");
        System.out.println("\t-i: from .csv file to .xml file");
    }
    /**
     *
     * @param args
     *
     * parser -i src dest
     * parse -o src dest
     */
    public static void main(String[] args) {
        if (args == null || args.length < 3) {
            help();
            return ;
        }

        String option = args[0];
        String xmlFileName, csvFileName;
        File xmlFile, csvFile;
        if ("-o".equals(option)) {
            xmlFileName = args[1];
            csvFileName = args[2];
            xmlFile = new File(xmlFileName);
            csvFile = new File(csvFileName);
            if (csvFile.exists()) {
                csvFile.delete();
            }
            try {
                csvFile.createNewFile();
                convertXmlToCsv(xmlFile, csvFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ;
        }

        if ("-i".equals(option)) {
            csvFileName = args[1];
            xmlFileName = args[2];
            xmlFile = new File(xmlFileName);
            csvFile = new File(csvFileName);
            if (!csvFile.exists()) {
                System.out.println("File " + csvFileName + " does not exist.");
                return ;
            }

            if (xmlFile.exists()) {
                xmlFile.delete();
            }

            try {
                xmlFile.createNewFile();
                convertCsvToXml(csvFile, xmlFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ;
        }

        help();
    }

    private static void convertCsvToXml(File csvFile, File xmlFile) {
        System.out.println("Converting CSV to XML file...");

        FileReader reader = null;
        BufferedReader bufferedReader = null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // root element: "resources"
            Element resources = document.createElement("resources");
            document.appendChild(resources);

            reader = new FileReader(csvFile);
            bufferedReader = new BufferedReader(reader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                final String[] splits = line.split(",");
                if (splits.length == 2) {
                    Element string = document.createElement("string");
                    string.setAttribute("name", splits[0]);
                    string.appendChild(document.createTextNode(splits[1]));
                    resources.appendChild(string);
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

            System.out.println("Finished.");

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private static void convertXmlToCsv(File xmlFile, File csvFile) {
        System.out.println("Converting XML to CSV file...");

        final String CSV_FORMAT = "%s,%s";

        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        try {
            writer = new FileWriter(csvFile);
            bufferedWriter = new BufferedWriter(writer);

            String output = String.format(CSV_FORMAT, "id", "text");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(xmlFile);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("string");
            int length = nodeList.getLength();
            for (int i = 0; i < length; ++i) {
                Element item = (Element)nodeList.item(i);
                String name = item.getAttribute("name");
                String textContent = item.getTextContent();
                output = String.format(CSV_FORMAT, name, textContent);
                //System.out.println(output);
                bufferedWriter.write(output);
                bufferedWriter.write("\n");
            }
            System.out.println("Finished.");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeSafely(bufferedWriter);
            IOUtils.closeSafely(writer);
        }
    }
}
