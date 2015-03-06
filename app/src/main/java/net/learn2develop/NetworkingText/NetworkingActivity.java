package net.learn2develop.NetworkingText;

import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.os.AsyncTask;
import android.widget.Toast;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.learn2develop.Networking.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class NetworkingActivity extends Activity {
    EditText et;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        et = (EditText)findViewById(R.id.editText);
       // String st = et.getText().toString();
        // ---access a Web Service using GET---
        et.setOnKeyListener(new View.OnKeyListener(){

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String st = et.getText().toString();
                    new AccessWebServiceTask().execute(st);

                }



                    return false;
            }
        });

        //new AccessWebServiceTask().execute("apple");
    }


    private InputStream OpenHttpConnection(String urlString)
            throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }

    private String WordDefinition(String word) {
        InputStream in = null;
        String strDefinition="";
        String text="";

        String currents="";
        try {
            in = OpenHttpConnection("http://services.aonaware.com/DictService/DictService.asmx/Define?word="
                    + word);

            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();
            parser.setInput(in,null);

            int eventType = parser.getEventType();

            while(eventType!=XmlPullParser.END_DOCUMENT){
                // Looking for a start tag
                    currents = parser.getName();
                if(eventType==XmlPullParser.TEXT){
                    text = parser.getText();
                }
                if(eventType==XmlPullParser.END_TAG) {
                    if (currents.equalsIgnoreCase("WordDefinition") && parser.getDepth() == 4) {
                        strDefinition = text;
                    }
                }

                   /*switch (eventType) {
                       // case XmlPullParser.START_DOCUMENT:
                       //    break;
                       case XmlPullParser.TEXT:
                           text = parser.getText();
                           break;

                       //  case XmlPullParser.START_TAG:
                       //   break;
                       case XmlPullParser.END_TAG:
                           if (currents.equalsIgnoreCase("WordDefinition") && parser.getDepth() == 4) {
                               strDefinition = text;
                           }
                           break;
                   }*/




                eventType=parser.next();
            }

            /*
            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db;
            try {
                db = dbf.newDocumentBuilder();
                doc = db.parse(in);
            } catch (ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            doc.getDocumentElement().normalize();

            // ---retrieve all the <Definition> elements---
            NodeList definitionElements = doc
                    .getElementsByTagName("Definition");

            // ---iterate through each <Definition> elements---
            for (int i = 0; i < definitionElements.getLength(); i++) {
                Node itemNode = definitionElements.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    // ---convert the Definition node into
                    // an Element---
                    Element definitionElement = (Element) itemNode;

                    // ---get all the <WordDefinition>
                    // elements under
                    // the <Definition> element---
                    NodeList wordDefinitionElements = (definitionElement)
                            .getElementsByTagName("WordDefinition");

                    strDefinition = "";
                    // ---iterate through each
                    // <WordDefinition> elements---
                    for (int j = 0; j < wordDefinitionElements
                            .getLength(); j++) {
                        // ---convert a <WordDefinition>
                        // node into an Element---
                        Element wordDefinitionElement = (Element) wordDefinitionElements
                                .item(j);

                        // ---get all the child nodes
                        // under the
                        // <WordDefinition> element---
                        NodeList textNodes = ((Node) wordDefinitionElement)
                                .getChildNodes();

                        strDefinition += ((Node) textNodes
                                .item(0))
                                .getNodeValue()
                                + ". \n";
                    }

                }
            }
        } catch (IOException e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
        }
            // ---return the definitions of the word---
            // return strDefinition;*/
       } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strDefinition;
    }

        private class AccessWebServiceTask extends
            AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return WordDefinition(urls[0]);
        }

        protected void onPostExecute(String result) {
            TextView tv = (TextView) findViewById(R.id.textView2);
            tv.setText(result);
        }
    }

}