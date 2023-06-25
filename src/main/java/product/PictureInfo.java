package product;

public class PictureInfo {
    private int idPicture;
    private String name;

    public PictureInfo(int idPicture, String name) {
        this.idPicture = idPicture;
        this.name = name;
    }

    public int getIdPicture() {
        return idPicture;
    }

    public String getName() {
        return name;
    }

    public void setIdPicture(int idPicture) {
        this.idPicture = idPicture;
    }

    public void setName(String name) {
        this.name = name;
    }
}
