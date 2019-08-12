package github.nisrulz.qreader;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ConexionWebService
{
    private static ConexionWebService instancia = null;
    private String NAMESPACE = "http://181.111.244.2/";
    private String URL = "http://181.111.244.2/Demows/wsestadodeuda.asmx";

    private ConexionWebService()
    {
    }

    public static ConexionWebService getInstancia()
    {
        return (instancia == null)? (instancia = new ConexionWebService()) : instancia;
    }


    public SoapObject getListaEventos()
    {
        String METHOD_NAME = "ListaEventos";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        SoapObject respuestaSoap = null;
        try
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            transporte.call(SOAP_ACTION, envelope);

            respuestaSoap = (SoapObject) envelope.getResponse();
        }
        catch(Exception ex)
        {
        }
        return respuestaSoap;
    }


    public SoapObject getVerificarContraseña(String dni, String contraseña)
    {
        
        String METHOD_NAME = "VerificarContraseña";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        SoapObject respuestaSoap = null;
        try
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("nroDNI", dni);
            request.addProperty("contraseña", contraseña);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL);

            transporte.call(SOAP_ACTION, envelope);
            respuestaSoap = (SoapObject) envelope.getResponse();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return respuestaSoap;
    }

    public SoapObject getEscribirAsistencia(String nroDoc, String codigoEvento)
    {

        String METHOD_NAME = "escribirAsistencia";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        SoapObject respuestaSoap = null;
        try
        {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("nroDoc", nroDoc);
            request.addProperty("codigoEvento", codigoEvento);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL);

            transporte.call(SOAP_ACTION, envelope);
            respuestaSoap = (SoapObject) envelope.getResponse();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return respuestaSoap;
    }

    public String getNAMESPACE() {
        return NAMESPACE;
    }

    public String getURL() {
        return URL;
    }

}
