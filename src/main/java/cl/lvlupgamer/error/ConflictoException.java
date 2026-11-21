package cl.lvlupgamer.error;




public class ConflictoException extends RuntimeException {

    private final String campo;

    public ConflictoException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
