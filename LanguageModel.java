import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;

    // The window length used in this model.
    int windowLength;

    // The random number generator used by this model. 
    private Random randomGenerator;

    /**
     * Constructs a language model with the given window length and a given
     * seed value. Generating texts from this model multiple times with the
     * same seed value will produce the same random texts. Good for debugging.
     */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /**
     * Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production.
     */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /**
     * Builds a language model from the text in the given file (the corpus).
     */
    public void train(String fileName) {
        String window = "";
        char c;
        In in = new In(fileName);
        for (int i = 0; i < windowLength; i++) {
            char tempChar = in.readChar();
            window += tempChar;
        }
        while (!in.isEmpty()) {
            c = in.readChar();
            List probs = CharDataMap.get(window);
            if (probs == null) {
                probs = new List();
                CharDataMap.put(window, probs);
            }
            probs.update(c);

            window = (window + c).substring(1);
        }

        for (List probs : CharDataMap.values()) {
            calculateProbabilities(probs);
        }
    }

    // Computes and sets the probabilities (p and cp fields) of all the
    // characters in the given list. */
    public void calculateProbabilities(List probs) {
        int allChars = 0;
        // Computes how many characters exist in total
        for (int i = 0; i < probs.getSize(); i++) {
            allChars += probs.listIterator(i).current.cp.count;
        }
        for (int i = 0; i < probs.getSize(); i++) {
            CharData currentChar = probs.listIterator(i).current.cp;
            CharData previousChar = (i == 0) ? null : probs.listIterator(i - 1).current.cp;
            currentChar.p = (double) currentChar.count / allChars;
            if (i == 0) {
                currentChar.cp = currentChar.p;
            } else {
                currentChar.cp = currentChar.p + previousChar.cp;
            }
        }
    }

    // Returns a random character from the given probabilities list.
   
    /**
     * Returns a string representing the map of this language model.
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (String key : CharDataMap.keySet()) {
            List keyProbs = CharDataMap.get(key);
            str.append(key + " : " + keyProbs + "\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        // Your code goes here
    }
}
