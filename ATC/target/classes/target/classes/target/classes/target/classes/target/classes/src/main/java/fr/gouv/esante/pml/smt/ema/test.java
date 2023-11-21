package fr.gouv.esante.pml.smt.ema;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class test {

	public static void main(String[] args) {
		
		String cmt = "ATC 2023 : code supprimé et remplacé par J07BN";
		
		  String[] tab =  cmt.split("par");
		  
		  System.out.println(tab[1]);
		
		/*
		 * SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); String
		 * dateInString = "01/01/2013";
		 * 
		 * try {
		 * 
		 * Date date = formatter.parse(dateInString); System.out.println(date);
		 * System.out.println(formatter.format(date));
		 * 
		 * } catch (ParseException e) { e.printStackTrace(); }
		 */

	}

}
