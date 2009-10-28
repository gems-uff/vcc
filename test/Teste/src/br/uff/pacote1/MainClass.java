package br.uff.pacote1;

public class MainClass {
	
	public static void main(String[] args) {
		Math.abs(10);
		Printer printer = new Printer();
		printer.print(10, 15.5);
		printer.print("Dez");
		
		br.uff.pacote2.Printer printer2 = new br.uff.pacote2.Printer();
		Float[] valores = {13.4f, 7.6f, 8f};
		printer2.print(10, valores);
		printer2.print("Dez");
		Math.acos(10);
	}
}
