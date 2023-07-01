package transactions;

// TODO: bring every record into this standard
// TODO: maybe bring all fetch classes into this file?
public class DataFetch {
    public final TransactionResult resultValue;

    protected DataFetch(TransactionResult result) {
        resultValue = result;
    }

    public boolean wasSuccessful() {
        return resultValue == TransactionResult.Successful;
    }
}
