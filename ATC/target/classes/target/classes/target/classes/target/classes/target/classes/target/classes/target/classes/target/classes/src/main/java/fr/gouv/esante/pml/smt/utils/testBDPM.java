package fr.gouv.esante.pml.smt.utils;

public class testBDPM {

	public static void main(String[] args) { // le point est tjrs obligat
		
		 String total = "1,02";
		
	
		
		System.out.println(convertVirguleFloat(total));

	}
	
	public static  String convertVirguleFloat(String xf) {
		
		if (xf == null) {
			return "";
		}
		
		else {
		String floatconvert = "";
		
		 String[] tabFloat = xf.split(",");
		 
		 //System.out.println(tabFloat.length);
		
		 for(int i=0; i<tabFloat.length-1; i++) {
			 
			 floatconvert = floatconvert+tabFloat[i];
		 }
		 
		 floatconvert =  floatconvert.concat(".").concat(tabFloat[tabFloat.length-1]);
		 return floatconvert;
		}
		
	}

}
