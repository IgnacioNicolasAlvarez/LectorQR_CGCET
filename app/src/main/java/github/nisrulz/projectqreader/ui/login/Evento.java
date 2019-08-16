package github.nisrulz.projectqreader.ui.login;

public class Evento {

    private String tema;
    private String fecha;
    private String turno;
    private String codigo;
    private String ConCosto;

    public Evento() {
    }

    public Evento(String tema, String fecha, String turno, String codigo, String conCosto ) {
        this.tema = tema;
        this.fecha = fecha;
        this.turno = turno;
        this.codigo = codigo;
        ConCosto = conCosto;

    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getConCosto() {
        return ConCosto;
    }

    public void setConCosto(String conCosto) {
        ConCosto = conCosto;
    }

}
