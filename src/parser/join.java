package parser;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

        import javax.xml.parsers.ParserConfigurationException;
        import javax.xml.parsers.SAXParser;
        import javax.xml.parsers.SAXParserFactory;
        import javax.xml.stream.XMLOutputFactory;
        import javax.xml.stream.XMLStreamException;
        import javax.xml.stream.XMLStreamWriter;

        import org.xml.sax.Attributes;
        import org.xml.sax.SAXException;
        import org.xml.sax.helpers.DefaultHandler;
        import org.w3c.tidy.Tidy;

public class join {


    public static void main(String[] args) throws Exception {

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        try {

            SAXParser saxParser = saxParserFactory.newSAXParser();

            XMLHandler handler = new XMLHandler();

            saxParser.parse(new File("output.xml"), handler);
            //Get authors list
            List<author> authList = handler.getAuthorList();

            outputHTML(authList);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        //"The open source JTidy project does an excellent job of converting HTML files to the newer XHTML standard."
        // The following code invokes JTidy programmatically:

        try {

            FileInputStream FIS = new FileInputStream("temp.html");

            FileOutputStream FOS = new FileOutputStream("output.html");

            Tidy T = new Tidy();

            T.setIndentCdata(true);

            T.setIndentContent(true);

            T.parse(FIS, FOS);

            FIS.close();

            FOS.close();

        } catch (java.io.FileNotFoundException e) {

            System.out.println(e.getMessage());

        }
    }


    private static void outputHTML(List<author> authList) throws Exception {

        String fileName = "temp.html";
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter xtw = null;

        try {

            xtw = xof.createXMLStreamWriter(new FileOutputStream(fileName), "UTF-8");
            xtw.writeStartDocument("utf-8", "1.0");
            xtw.setPrefix("html", "http://www.w3.org/TR/REC-html40");
            xtw.writeStartElement("html");
            xtw.writeNamespace("html", "http://www.w3.org/TR/html4/loose.dtd");

            xtw.writeStartElement("head");

            xtw.writeStartElement("title");

            xtw.writeCharacters("Renaissance Artists");

            xtw.writeEndElement();

            xtw.writeStartElement("style");
            xtw.writeAttribute("type", "text/css");

            writeCascadingStyleSheet(xtw);


            xtw.writeEndElement();

            xtw.writeStartElement("body");

            //content begins here
            xtw.writeStartElement("h1");
            xtw.writeCharacters("Renaissance Artists");
            xtw.writeEndElement();

            xtw.writeStartElement("table");
            xtw.writeAttribute("border", "1");

            xtw.writeStartElement("tbody");


            // header row
            xtw.writeStartElement("tr");

            xtw.writeStartElement("th");
            xtw.writeAttribute("colspan", "5");
            xtw.writeCharacters("Name");
            xtw.writeEndElement();


            xtw.writeStartElement("th");
            xtw.writeAttribute("colspan", "5");
            xtw.writeCharacters("Nationality");
            xtw.writeEndElement();


            xtw.writeStartElement("th");
            xtw.writeAttribute("colspan", "5");
            xtw.writeCharacters("Year Born");
            xtw.writeEndElement();


            xtw.writeStartElement("th");
            xtw.writeAttribute("colspan", "5");
            xtw.writeCharacters("Year Died");
            xtw.writeEndElement();


            xtw.writeStartElement("th");
            xtw.writeAttribute("colspan", "5");
            xtw.writeCharacters("Artworks form");
            xtw.writeEndElement();


            xtw.writeEndElement();//closes the row tag


            // end of header row


            // add each author to HTML table.
            for (author auth : authList) {
                xtw.writeStartElement("tr");


                xtw.writeStartElement("td");
                xtw.writeAttribute("class", "right");
                xtw.writeAttribute("colspan", "5");
                xtw.writeCharacters(auth.getName());
                xtw.writeEndElement();


                xtw.writeStartElement("td");
                xtw.writeAttribute("class", "right");
                xtw.writeAttribute("colspan", "5");
                xtw.writeCharacters(auth.getNationality());
                xtw.writeEndElement();


                xtw.writeStartElement("td");
                xtw.writeAttribute("class", "right");
                xtw.writeAttribute("colspan", "5");
                xtw.writeCharacters(auth.getBirthDate());
                xtw.writeEndElement();


                xtw.writeStartElement("td");
                xtw.writeAttribute("class", "right");
                xtw.writeAttribute("colspan", "5");
                xtw.writeCharacters(auth.getDeceasedDate());
                xtw.writeEndElement();


                xtw.writeStartElement("td");
                xtw.writeAttribute("class", "right");
                xtw.writeAttribute("colspan", "5");
                xtw.writeCharacters(auth.getArtForms());
                xtw.writeEndElement();


                xtw.writeEndElement(); // closes the row tag


            }


            //content ends here
            xtw.writeEndElement();

            xtw.writeEndElement();

            xtw.writeEndElement();

            xtw.writeEndDocument();

            System.out.println("Done");


        } catch (XMLStreamException e) {
            e.printStackTrace();
        } finally {
            if (xtw != null) {
                try {
                    xtw.flush();
                    xtw.close();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // Writes the CSS as an embedded style tag to the file.
    private static void writeCascadingStyleSheet(XMLStreamWriter writer) throws XMLStreamException {
        writeCascadingStyleSheetRules(writer, "body", new String[]{"font-family: Verdana, Sans-Serif;"});
        writeCascadingStyleSheetRules(writer, "table", new String[]{"table-layout:fixed"});
        writeCascadingStyleSheetRules(writer, "th, td", new String[]{"font-weight: normal;", "padding: .5em;"});
        writeCascadingStyleSheetRules(writer, "th[colspan]", new String[]{"font-weight: bold;", "background-color: grey;", "text-align: left;"});
        writeCascadingStyleSheetRules(writer, "td[colspan]", new String[]{"text-align: right;", "font-weight: bold;"});
        writeCascadingStyleSheetRules(writer, ".right", new String[]{"text-align: right;"});
    }

    // Writes a CSS rule to the file.
    //acknowledgment for example: Dale Vassialidis


    private static void writeCascadingStyleSheetRules(XMLStreamWriter xtw, String selector, String[] rules) throws XMLStreamException {
        // Writes selector and opening brace.

        xtw.writeCharacters(selector);

        xtw.writeCharacters("{");


        // Iterates over each rule and writes to file.
        for (int rule = 0; rule < rules.length; ++rule) {
            xtw.writeCharacters(rules[rule]);
        }
        // Writes closing brace.

        xtw.writeCharacters("}");


    }

}

class author {

    private String name;
    private String nationality;
    String births_deaths[] = new String[2];
    private int count = 0;
    private List<String> artforms = null;

    public void matchDates(String line) {

        String pattern = "(\\d{4})";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Create matcher object.
        Matcher m = r.matcher(line);

        while (m.find()) {


            this.setLifetime(m.group());


        }

    }

    public void setDates(String date_string) {

        matchDates(date_string);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setLifetime(String date) {


        births_deaths[count] = date;

        count++;


    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nation) {
        this.nationality = nation;
    }

    public String getBirthDate() {

        return this.births_deaths[0];

    }


    public String getDeceasedDate() {

        return this.births_deaths[1];

    }

    public String getArtForms() {
        if (artforms != null) {
            StringBuilder sb = new StringBuilder();
            for (String form : artforms) {
                if (artforms.indexOf(form) != (artforms.size() - 1)) {
                    sb.append(form + ", ");
                } else {

                    sb.append(form);

                }
            }

            return sb.toString();
        } else return null;

    }

    public void addArtForm(String form) {

        //initialize list
        if (artforms == null) {
            artforms = new ArrayList<>();
            artforms.add(form);
        } else {

            artforms.add(form);

        }

    }


}


class XMLHandler extends DefaultHandler {

    //List to hold author objects
    private List<author> authorList = null;

    private author auth = null;


    //getter method for author list
    public List<author> getAuthorList() {

        return authorList;

    }


    boolean bName = false;

    boolean bDates = false;

    boolean bNationality = false;

    boolean bArtworks = false;


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

        if (qName.equalsIgnoreCase("author")) {

            //initialize author object
            auth = new author();

            //initialize list
            if (authorList == null)
                authorList = new ArrayList<>();

        } else if (qName.equalsIgnoreCase("name")) {

            //set boolean values for fields, will be used in setting author variables
            bName = true;

        } else if (qName.equalsIgnoreCase("born-died")) {

            bDates = true;

        } else if (qName.equalsIgnoreCase("nationality")) {

            bNationality = true;

        } else if (qName.equalsIgnoreCase("artworks")) {

            bArtworks = true;

            String att = attributes.getValue("form");

            auth.addArtForm(att);

        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("author")) {
            //add author object to list
            authorList.add(auth);
        }
    }


    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        if (bName) {

            //name element, set author name

                auth.setName(new String(ch, start, length));

                bName = false;


        } else if (bDates) {

            auth.setDates(new String(ch, start, length));

            bDates = false;

        } else if (bNationality) {

            auth.setNationality(new String(ch, start, length));

            bNationality = false;

        } else if (bArtworks) {

            bArtworks = false;
        }
    }
}