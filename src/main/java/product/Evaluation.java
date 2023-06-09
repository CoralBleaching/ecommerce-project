package product;

import java.time.LocalDate;

public class Evaluation {
    int idProduct,
            idSale,
            score;
    String timestamp,
            review;

    public Evaluation() {
    }

    public Evaluation(int idProduct, int idSale, int score, String timestamp, String review) {
        this.idProduct = idProduct;
        this.idSale = idSale;
        this.score = score;
        this.timestamp = timestamp;
        this.review = review;
    }

    public Evaluation(Evaluation other) {
        this.idProduct = other.idProduct;
        this.idSale = other.idSale;
        this.score = other.score;
        this.timestamp = other.timestamp;
        this.review = other.review;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public int getIdSale() {
        return idSale;
    }

    public int getScore() {
        return score;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getReview() {
        return review;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public void setIdSale(int idSale) {
        this.idSale = idSale;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setReview(String review) {
        this.review = review;
    }

}
