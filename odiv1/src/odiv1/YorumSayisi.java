package odiv1;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class YorumSayisi {
    private String regexTekSatir ="//.+";
    private String regexCokSatir ="/\\*[^*](\\v|.|\\n)*?\\*/";
    private String regexJavadoc ="/\\*[*](\\v|.|\\n)*?\\*/";
    private String regexClass ="(?<=(public|private|protected)?\\s?.*class\\s).*?(?=\\s)";
    private String regexFonksiyon ="(?<=(public|private|protected)?\\s?(static)?\\s?(byte|short|int|long|float|double|boolean|void|String)?\\s?)\\b\\w+\\b(?=\\(.*\\))";
    private String txt;
    private String satir;
    private int susluParantezSayisi = 0;
    
    Pattern oruntuTekSatir = Pattern.compile(regexTekSatir);
    Pattern oruntuCokSatir = Pattern.compile(regexCokSatir);
    Pattern oruntuJavadoc = Pattern.compile(regexJavadoc);
    Pattern oruntuClass = Pattern.compile(regexClass);
    Pattern oruntuFonksiyon = Pattern.compile(regexFonksiyon);
    
    Matcher eslesmeTekSatir,eslesmeCokSatir,eslesmeClass, eslesmeJavadoc, eslesmeFonksiyon;    
    
    ArrayList<String> listTekSatir = new ArrayList<>();
    ArrayList<String> listCokSatir = new ArrayList<>();
    ArrayList<String> listJavadoc = new ArrayList<>();
    
    BufferedReader reader;
    
    private int adetTek, adetCok, adetJavadoc;
    private String fonlsiyonAdi = "";
    private String sonHal;
    
    public YorumSayisi(String dosyaAdi) throws IOException {
    	reader = new BufferedReader(new FileReader(dosyaAdi));
    }

    private void sonHalSet(String sonHal)
    {
    	this.eslesmeTekSatir = oruntuTekSatir.matcher(sonHal);
        this.eslesmeCokSatir = oruntuCokSatir.matcher(sonHal);
        this.eslesmeClass = oruntuClass.matcher(sonHal);
        this.eslesmeJavadoc = oruntuJavadoc.matcher(sonHal);
        this.eslesmeFonksiyon = oruntuFonksiyon.matcher(sonHal);
    }
    
    private void KacTaneCommentVar(String sonHal) {
    	while(true) {
    		if(eslesmeTekSatir.find()) {
    			this.adetTek++;		
    			listTekSatir.add(eslesmeTekSatir.group());
    		} else if(eslesmeCokSatir.find()) {
    			this.adetCok++;
    			listCokSatir.add(eslesmeCokSatir.group());
    		} else if(eslesmeJavadoc.find()) {
    			this.adetJavadoc++;
    			listJavadoc.add(eslesmeJavadoc.group());
    		} else
    			break;
    	}
    }
    public void HariciDosyalaraKaydet(String fonlsiyonAdi) throws IOException {
    	FileWriter fosTeksatir = new FileWriter("teksatir.txt", true);
    	FileWriter fosCoksatir = new FileWriter("coksatir.txt", true);
    	FileWriter fosJavadoc = new FileWriter("javadoc.txt", true);
    	if(listTekSatir.size() != 0) {
    		fosTeksatir.write("fonlsiyonAdi :" + fonlsiyonAdi + "\n");
        	for(String tekSatir : listTekSatir ) {
        		fosTeksatir.write("\n" +tekSatir + "\n");	
        	}
        	fosTeksatir.write("---------------------------------------------------\n");
        	fosTeksatir.close();
    	}
    	if(listCokSatir.size() != 0) {
    		fosCoksatir.write("fonlsiyonAdi :" + fonlsiyonAdi + "\n");
        	for(String cokSatir : listCokSatir ) {
        		fosCoksatir.write("\n" + cokSatir + "\n");
        	}
        	fosCoksatir.write("---------------------------------------------------\n");
        	fosCoksatir.close();
    	}
    	if(listJavadoc.size() != 0) {
    		fosJavadoc.write("fonlsiyonAdi :" + fonlsiyonAdi+ "\n");
        	for(String javadoc : listJavadoc ) {
        		fosJavadoc.write("\n" +javadoc + "\n");
        	}
        	fosJavadoc.write("---------------------------------------------------\n");
    		fosJavadoc.close();
    	}
    }
    public void basla() throws IOException 
    {
    	while((satir = reader.readLine()) != null) 
    	{
    		txt += "\n" + satir;
    		this.eslesmeClass = oruntuClass.matcher(txt);
    		if(eslesmeClass.find()) 
    		{
    			System.out.println("sinif : "+ eslesmeClass.group());
    			while((satir = reader.readLine()) != null) 
    			{
    				txt += "\n" + satir;
    				this.eslesmeFonksiyon = oruntuFonksiyon.matcher(satir);
    				if(eslesmeFonksiyon.find()) 
    				{
    					adetTek = adetCok = adetJavadoc = 0;
    	    			listTekSatir.clear();
    	    			listCokSatir.clear();
    	    			listJavadoc.clear();
    					fonlsiyonAdi = eslesmeFonksiyon.group();
    					if(satir.contains("{"))
    						susluParantezSayisi++;
    					if(satir.contains("}"))
    						susluParantezSayisi--;
    					while((satir = reader.readLine()) != null) 
    					{
    						txt += "\n" + satir;
    						if(satir.contains("{"))
    							susluParantezSayisi++;
    						if(satir.contains("}"))
    							susluParantezSayisi--;
    						if(susluParantezSayisi == 0)
    							break;
    					}
    					sonHalSet(txt);
    					KacTaneCommentVar(sonHal);
    					HariciDosyalaraKaydet(fonlsiyonAdi);
    					System.out.println("\tFonksiyon: "+ fonlsiyonAdi 
    	    					+ "\n\t\tTek stir Yorum Sayisi :"+ adetTek
    	    					+ "\n\t\tCok stir Yorum Sayisi :"+ adetCok
    	    					+ "\n\t\tJavadoc  Yorum Sayisi :"+ adetJavadoc
    	    					+ "\n-------------------------------------------");
    					txt ="";
    				}
    			}
    		}
    	}
    }
}