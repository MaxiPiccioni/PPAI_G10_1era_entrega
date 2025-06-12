package Clases;

public class MotivoFueraServicio {
    private String comentario;
    private MotivoTipo motivoTipo;

    public MotivoFueraServicio(String comentario, MotivoTipo motivoTipo) {
        this.comentario = comentario;
        this.motivoTipo = motivoTipo;
    }


    public String getComentario() {
        return comentario;
    }


    public MotivoTipo getMotivoTipo() {
        return motivoTipo;
    }


    public void setComentario(String comentario) {
        this.comentario = comentario;
    }


    public void setMotivoTipo(MotivoTipo motivoTipo) {
        this.motivoTipo = motivoTipo;
    }
}
