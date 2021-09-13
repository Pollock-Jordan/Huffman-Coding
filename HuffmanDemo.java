import java.io.IOException;
import java.io.PrintWriter;

public class HuffmanDemo extends Huffman{
	
	public static void main(String[] args) throws IOException {
		//Prompt the user for a text file to encode
		encode();
		
		//Prompt the user for a text file to decode
		decode();
	}
}
