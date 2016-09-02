package com.arsnindo.wikicorpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.* ;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Category;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.model.Page.PageType;
import org.wikipedia.miner.util.WikipediaConfiguration;

import com.arsnindo.util.Cleaner;
import com.arsnindo.util.Connect;
import com.arsnindo.util.tempBox;



public class Wikicorpus {
		
		static Wikipedia _wikipedia;
		BufferedWriter out;
		BufferedReader _input;
		Cleaner c = new Cleaner();
		Connect connect = new Connect();
		
		
public Wikicorpus(WikipediaConfiguration conf){ 
		// memuat configurasi wiki, untuk akses ke database barkeley
		_wikipedia = new Wikipedia(conf, false);
	}
	
/*
 * @param file adalah sebuah file yang berisi seluruh id artikel, category, redirect, disambigu dari wikipedia
 * @param wikipedia adalah env wikipedia yang sedang dijalankan  
 */

public void ArticleSet(File file, Wikipedia wikipedia) throws Exception{
	
	BufferedReader reader = new BufferedReader(new FileReader(file)) ;
	String line  ;

	
	while ((line = reader.readLine()) != null) {
		String[] values = line.split("\t") ;
		int id = new Integer(values[0].trim()) ;
		Article article = (Article) wikipedia.getPageById(id);
		displayMarkup(article);
		
		}
	reader.close();
}

/*
 * @param isi adalah page yang ingin dibersihkan markupnya dan tambah ke dalam file
 * 
 */

private void displayMarkup(Page isi)throws Exception {
	Cleaner markupclean = new Cleaner();
	String hasil = markupclean.getSentenceClean(isi);
	try{
	out = new BufferedWriter(new FileWriter("T:/Project2/Corpus-FULL.xml",true));

	out.append(hasil);
	out.newLine();
	out.newLine();
    out.close();
	}catch(IOException e){
		System.out.println("There was a problem:" + e);
	}
}

/*
 * @param p adalah page yang akan diambil title dan id nya yang kemudian dimasukan kedalam file
 * 
 */

protected void deskrip(Page p) throws Exception {
	String title = p.getTitle();
	int id = p.getId();
	
	try{
		out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/templateById.tsv",true));
		out.append(String.format("%-15s %-15s %n", id, title));
		out.close();
		System.out.println("Artikel dengan " +id+ " telah di deskrip");
		}catch(IOException e){
			System.out.println("There was a problem:" + e);
		}
}


private void deskriptor() throws Exception{
	Iterator<Page> iter = _wikipedia.getPageIterator(PageType.article); 
	Page p ;
	
	while(iter.hasNext()){
		Page p1 = iter.next();
		p=p1;
		deskrip(p);	
	}
}


/*
 * method untuk melakukan iterasi terhadap artikel yang ada kemudian di bersihkan (membuat corpus)
 */


private void corpusClean()  throws Exception {
	Iterator<Page> iter = _wikipedia.getPageIterator(PageType.article); 
	Page p ;
	while(iter.hasNext()){
		Page p1 = iter.next();
		p=p1;
		displayMarkup(p);	
	}
}

/*
 * method bantuan untuk memabersihkan artikel dan mengambil box nya
 */

private void box(Page p) throws Exception{
	Cleaner c = new Cleaner();
	String hasil = c.getBox(p);
	
	int id = p.getId();
	
	/*
	 * tidak semua artikel memiliki infobox
	 */
	if (hasil == ""){
		
		try{
			out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/box/nobox/list.nobox.txt", true));
			out.append(String.format("%-15s %-15s %n", p.getId(), p.getTitle()));
			out.newLine();
			out.close();
			System.out.println( id+ " tidak mempunyai box");
		}catch (IOException e){
			System.out.println("There was a problem:" + e);
		}
		
	}else{
		
		try{
			out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/box/"+ id + ".box.txt"));
			out.write(hasil);
			out.newLine();
			out.close();
			System.out.println("Artikel dengan id " +id+ " telah di diextract boxnya");
			}catch(IOException e){
				System.out.println("There was a problem:" + e);
			}
	
	}
}


/*
 * method untuk mengambil info box dari wikipedia
 */

private void boxer() throws Exception {
	Iterator<Page> iter = _wikipedia.getPageIterator(PageType.article); 
	Page p ;
	while(iter.hasNext()){
		Page p1 = iter.next();
		p=p1;
		box(p);	
	}
}

private void doc(Page p) throws IOException{
	Cleaner c = new Cleaner();
	String hasil = c.getDesc(p);
	int id = p.getId();
	
	out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/document/"+ id + ".doc.txt"));
	out.write(hasil);
	out.newLine();
	out.close();
	System.out.println("Artikel dengan id " +id+ " telah di diextract boxnya");
	
}

private void docer() throws IOException {
	 Page article = _wikipedia.getPageById(44);
	 doc(article);
	 
//     System.out.println(article.getType());
//     System.out.println(article.getTitle());
//     System.out.println(article.getMarkup());
//     System.out.println(article.getFirstParagraphMarkup());
	
}

private String ambilKategori(String ask, String p, int id) throws IOException{
	Page kategori = _wikipedia.getCategoryByTitle(p);
	String judul = null;
	String query = ask;
	String hasil = null;
	Cleaner c = new Cleaner();
	
	
	if(kategori != null){
		judul = kategori.getTitle();
		if(c.isKategori(judul, query)){
			hasil = judul;
		}else
			hasil = null;
		}
	else
		hasil = null;
	
	return hasil;
}

private Integer getInt(String prompt, int min, int max) throws IOException {
	
	while (true){
		
		System.out.println(prompt + " (" + min + " - " + max + " atau ENTER untuk mengkosongkan)");
		
		String line = _input.readLine();
		if (line.trim().equals(""))
			return null;
		
		try{
			Integer val = Integer .parseInt(line);
			if (val >= min && val <= max)
                return val ;
		}catch (Exception e){
			
		}
		
		System.out.println("Input tidak tepat, silakan mencoba lagi");
				
	}
			
}

private String getString(String prompt) throws IOException{
	System.out.println(prompt + "(atau ENTER untuk mengosongkan)");
	String line = _input.readLine();
	if (line.trim().equals(""))
		return null;
	return line;
}

/*
 * delay 
 * @param n time in miliseconds
 */

private void delay(long n){
	try{
	Thread.sleep(n);
	}catch (InterruptedException e){
		
	}
}

// get category without matching query

private String ambilKategoriWOQuery(String p) throws IOException{
	Page kategori = _wikipedia.getCategoryByTitle(p);
	String judul = null;
	String hasil = null;
	
	
	if(kategori != null){
		judul = kategori.getTitle();
		hasil = judul;
		}
	else
		hasil = null;
	
	return hasil;
}

/*
 * insert all (only-content), (only-box), (only-category) to DATABASE
 */

public void inputToDatabase(String startCat, String endCat){
	_input = new BufferedReader(new InputStreamReader(System.in));
	Page p;
	List<String> kategoriTerkait = new ArrayList<String>();
	List<String> childTerkait = new ArrayList<String>();
	List<tempBox> label = null ;
	connect.connect();
	int contact = 0;
	

		int k = 0;
		Iterator<Page> iter = _wikipedia.getPageIterator(PageType.category);
		System.out.println("\nMohon tunggu~\n");
			for (k=0; k<66889; k++){
				Page p1 = iter.next();
				p=p1;
				try{
				String isi = ambilKategoriWOQuery(p.getTitle());
				/*
				 * to resume when iterator find Tokoh Wales
				 * un-comment an change contact value to 0 before use this
				 */
				if (isi.equals(startCat)){
					contact = 1;
				}
				
				/*
				 * end point
				 */
				if (isi.equals(endCat)){
					contact = 0;
				}
				
				if(contact == 1){
					if (isi != null){
						kategoriTerkait.add(isi);
						if(k == 66889)
							((BufferedReader) iter).close();
					}
				}
				
				}catch(IOException e){
					System.err.println("Terjadi masalah ketika loop kategory");
					
				}
			}
			

			for (int i=0 ; i<kategoriTerkait.size() ; i++) {
				System.out.println("** Kategori '" +  kategoriTerkait.get(i) + "' - eksraksi dimulai");
				
				Category kategori = _wikipedia.getCategoryByTitle(kategoriTerkait.get(i));
				Category[] listChild = kategori.getChildCategories();
				Article[] listArtikel = kategori.getChildArticles();
				String hasil = "";
				
				/*
				 * to handle if some category have child category
				 */
				if (listChild.length != 0){
					for (int l=0 ; l<listChild.length ; l++) {
						childTerkait.add(listChild[l].getTitle());
					}
				}
				
//				for (int j=0 ; j<listArtikel.length ; j++) { 
//					System.out.println("	- "+listArtikel[j].getId()+"- Artikel '" +  listArtikel[j].getTitle() + "' - DIEKSTRAK");
//					
//					// try BATCH MODE DOC-CAT
//					
////					try{
////						hasil = c.getDesc(listArtikel[j]);
////						}catch(StackOverflowError SO){
////							System.err.println(SO);
////						}
////					
////					connect.docBatch(listArtikel[j].getId(), listArtikel[j].getTitle(), hasil);
////					connect.catBatch(kategoriTerkait.get(i), listArtikel[j].getId());
//					
//					/////////////////////////////////
//					
//					// try BATCH MODE BOX 
////					try{
////						label = c.getBox2(listArtikel[j]);
////						}catch(StackOverflowError SO){
////							System.err.println(SO);
////						}
////						
////						
////					try{
////						if(label.size() == 0){
////							System.err.println("Artikel dengan id " + listArtikel[j].getId() + "tidak mempunyai box" );
////						}else{
////							for (int a = 0; a < label.size(); a++){
////								
////								for( int b=0; b < label.get(a).getLabel().size(); b++){
////									
////								connect.boxBatch(listArtikel[j].getId(), label.get(a).getLabel().get(b), label.get(a).getValue().get(b), label.get(a).getType(), label.get(a).getKategori());
////									
////								}
////								
////							}
////							
////							
////						}
////						
////			           }catch(StackOverflowError e){
////			        	   System.err.println(e);
////			           }
//					
//					////////////////////////////////
//						
//					// get document and category file
////					try{
////					hasil = c.getDesc(listArtikel[j]);
////					}catch(StackOverflowError SO){
////						System.err.println(SO);
////					}
////					
////					connect.createDoc(listArtikel[j].getId(), listArtikel[j].getTitle(), hasil);
////					connect.createCat(kategoriTerkait.get(i), listArtikel[j].getId());
//					
//					/////////////////////////////////
//					
//					// get every label on every infobox with it's value 
////					try{
////					label = c.getBox2(listArtikel[j]);
////					}catch(StackOverflowError SO){
////						System.err.println(SO);
////					}
////					
////					
////					try{
////					if(label.size() == 0){
////						System.err.println("Artikel dengan id " + listArtikel[j].getId() + "tidak mempunyai box" );
////					}else{
////						for (int a = 0; a < label.size(); a++){
////							
////							for( int b=0; b < label.get(a).getLabel().size(); b++){
////								
////							connect.createBox(listArtikel[j].getId(), label.get(a).getLabel().get(b), label.get(a).getValue().get(b), label.get(a).getType(), label.get(a).getKategori());
////								
////							}
////							
////						}
////						
////						
////					}
////					
////					
////					
////		           }catch(StackOverflowError e){
////		        	   System.err.println(e);
////		           }
//					///////////////////////////////////////////
//					
//				delay(10);
//					
//				}
				
//				System.out.println("* Kategori '" +  kategoriTerkait.get(i) + "' - ekstraksi selesai");
			
			}
			
			/*
			 * only do for child category
			 */
			
			for (int i=0 ; i<childTerkait.size() ; i++) {
				System.out.println("** Kategori '" +  childTerkait.get(i) + "' - eksraksi dimulai");
				
				Category kategori = _wikipedia.getCategoryByTitle(childTerkait.get(i));
				Article[] listArtikel = kategori.getChildArticles();
				String hasil = "";
				
				for (int j=0 ; j<listArtikel.length ; j++) { 
					System.out.println("	- "+listArtikel[j].getId()+"- Artikel '" +  listArtikel[j].getTitle() + "' - DIEKSTRAK");
					
					// try BATCH MODE DOC-CAT
					
					try{
						hasil = c.getDesc(listArtikel[j]);
						}catch(StackOverflowError SO){
							System.err.println(SO);
						}
					
					connect.docBatch(listArtikel[j].getId(), listArtikel[j].getTitle(), hasil);
					connect.catBatch(childTerkait.get(i), listArtikel[j].getId());
					
					/////////////////////////////////
					
					// try BATCH MODE BOX 
					try{
						label = c.getBox2(listArtikel[j]);
						}catch(StackOverflowError SO){
							System.err.println(SO);
						}
						
						
					try{
						if(label.size() == 0){
							System.err.println("Artikel dengan id " + listArtikel[j].getId() + "tidak mempunyai box" );
						}else{
							for (int a = 0; a < label.size(); a++){
								
								for( int b=0; b < label.get(a).getLabel().size(); b++){
									
								connect.boxBatch(listArtikel[j].getId(), label.get(a).getLabel().get(b), label.get(a).getValue().get(b), label.get(a).getType(), label.get(a).getKategori());
									
								}
								
							}
							
							
						}
						
			           }catch(StackOverflowError e){
			        	   System.err.println(e);
			           }
					
					////////////////////////////////
					
				delay(10);
					
				}
				
				System.out.println("* Kategori '" +  childTerkait.get(i) + "' - ekstraksi selesai");
			
			}
			
			//  EXE BATCH STATEMENT
			connect.exeDocBatch();
			connect.exeCatBatch();
			connect.exeBoxBatch();
			/////////////////////
			System.out.println("jumlah child kategory yang di ekstrak adalah "+ childTerkait.size());
			kategoriTerkait.clear();
			childTerkait.clear();
}


public void getArticleByCategory() throws Exception{
	_input = new BufferedReader(new InputStreamReader(System.in));
	Page p;
	String termKategori;
	Page penampung;
	List<String> kategoriTerkait = new ArrayList<String>(); 
	
	while((termKategori = getString("Silakan masukan kategori yang anda mau cari")) != null){
		int k = 0;
		Iterator<Page> iter = _wikipedia.getPageIterator(PageType.category);
		System.out.println("\nMohon tunggu~\n");
			for (k=0; k<66889; k++){
				Page p1 = iter.next();
				p=p1;
				String isi = ambilKategori(termKategori, p.getTitle(), p.getId());
				if (isi != null){
				kategoriTerkait.add(isi);
				if(k == 66889)
					((BufferedReader) iter).close();
				}
			}
			

			if(kategoriTerkait.size() == 0){
					System.out.println("\n== Mohon maaf kata kunci untuk kategori yang anda cari tidak ada ==\n");
			}else{
				System.out.println("== term kategori " + termKategori + " ada di dalam beberapa kategori yaitu :");
				
				for (int i=0 ; i<kategoriTerkait.size() ; i++) {
		            System.out.println(" - [" + (i+1) + "] " + kategoriTerkait.get(i));
		        }	
				
				Integer kategoriIndex = getInt("Pilih Kategori yang anda cari", 1, kategoriTerkait.size());
//				try{
				Category kategori = _wikipedia.getCategoryByTitle(kategoriTerkait.get(kategoriIndex-1));
				Article[] listArtikel = kategori.getChildArticles();
				
				
				if (listArtikel.length == 0) {
			    	   System.out.println("\n!! Kategori '" + kategoriTerkait.get(kategoriIndex-1) + "' tidak mempunyai turunan artikel !!");
			       }else {
			    	   System.out.println("\n=== Turunan artikel untuk Kategori '" + kategoriTerkait.get(kategoriIndex-1) + "' yaitu : ===");
			           for (int i=0 ; i<listArtikel.length ; i++) { 
			               System.out.println(" - [" + (i+1) + "] " + listArtikel[i].getTitle()) ;
			           }
			           Integer artikelIndex = getInt("\nPilih artikel yang anda cari", 1, listArtikel.length);
			           penampung = _wikipedia.getArticleByTitle(listArtikel[artikelIndex-1].getTitle());
					   System.out.println("\n" + penampung.getMarkup()+ "\n");
			           
			       }
			}
			
			kategoriTerkait.clear();
	}
}


/*
 * @param id adalah id dari artikel yang ingin dilihat kategorinya
 * 
 */

public void getCategoryParentChild(int id){
	
	Page article = _wikipedia.getPageById(id);
//    System.out.println(article.getType());
//    System.out.println(article.getTitle());
    
    
    /// WORKSPACE ///
    
    Category kategori = _wikipedia.getCategoryByTitle(article.getTitle());
    
    if(kategori != null){
    	
    	System.out.println(kategori.getId());
    	System.out.println(kategori.getTitle());
    }
    
   Category[] tempChild  = kategori.getChildCategories();
   Category[] tempParent = kategori.getParentCategories();
   
   if (tempChild.length == 0) {
	   System.out.println("=== Kategori ini tidak mempunyai turunan kategori ===");
   } else {
	   System.out.println("=== Kategori Turunan untuk Kategori ini yaitu : ===");
       for (int i=0 ; i<tempChild.length ; i++) { 
           System.out.println(" - [" + (i+1) + "] " + tempChild[i].getTitle()) ;
       }
       
   }
   
   System.out.println("\n") ;
   
   if (tempParent.length == 0) {
	   System.out.println("=== Kategori ini tidak mempunyai parent kategori ===");
   } else {
	   System.out.println("=== Kategori parent untuk Kategori ini yaitu : ===");
       for (int i=0 ; i<tempParent.length ; i++) {
    	   
           System.out.println(" - [" + (i+1) + "] " + tempParent[i].getTitle()) ;
       }
       
   }
}

	public void getDescFromArticle() throws Exception{
		_input = new BufferedReader(new InputStreamReader(System.in));
		Page p;
		String termKategori;
		Page penampung;
		List<String> kategoriTerkait = new ArrayList<String>(); 
		int counter = 0;
		
		while((termKategori = getString("Silakan masukan kategori yang anda mau cari")) != null){
			int k = 0;
			Iterator<Page> iter = _wikipedia.getPageIterator(PageType.category);
			System.out.println("\nMohon tunggu~\n");
				for (k=0; k<66889; k++){
					Page p1 = iter.next();
					p=p1;
					String isi = ambilKategori(termKategori, p.getTitle(), p.getId());
					if (isi != null){
					kategoriTerkait.add(isi);
					if(k == 66889)
						((BufferedReader) iter).close();
					}
				}
				
				for (int i=0 ; i<kategoriTerkait.size() ; i++) {
					System.out.println("* Kategori '" +  kategoriTerkait.get(i) + "' - eksraksi dimulai");
					
					Category kategori = _wikipedia.getCategoryByTitle(kategoriTerkait.get(i));
					Category[] listChild = kategori.getChildCategories();
					Article[] listArtikel = kategori.getChildArticles();
					String hasil = "";
					
					/*
					 * to handle if soe
					 */
					if (listChild.length != 0){
						for (int l=0 ; l<listChild.length ; l++) {
							kategoriTerkait.add(listChild[l].getTitle());
						}
					}
					
					for (int j=0 ; j<listArtikel.length ; j++) { 
						System.out.println("	- Artikel '" +  listArtikel[j].getTitle() + "' - ekstraksi dimulai");
						hasil = c.getDesc(listArtikel[j]);
						
						//MENGAMBIL KONTEN ARTIKEL//
						
						out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/dalam tahun 2015/doc/"+ listArtikel[j].getId() + ".doc.txt"));
						out.write(hasil);
						out.newLine();
						out.close();
						
						//------------------------//
						
						//MENGAMBIL BOX ARTIKEL   //
						
//						hasil = c.getBox(listArtikel[j]);
//						if(hasil != ""){
//							try{	
//							out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/dalam tahun 2015/box/"+ listArtikel[j].getId() + ".box.txt"));
//							out.write(hasil);
//							out.newLine();
//							out.close();
//							}catch(NullPointerException e){
//								System.err.println(listArtikel[j].getTitle() + " dan " +listArtikel[j].getId()+ " Tidak mempunyai markup");
//							}
//						}else{
//							out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/dalam tahun 2015/box/nobox/NoBox.txt", true));
//							out.append(String.format("%-15s %-15s %-15s %-15s %n", counter, kategoriTerkait.get(i), listArtikel[j].getId() , listArtikel[j].getTitle()));
//							out.newLine();
//							out.close();
//						}
						
						//-------------------------//
						
						//MENGAMBIL LIST ARTIKEL//
//						counter++ ;
//						out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/dalam tahun 2015/doc/test/listArtikel.txt", true));
//						out.append(String.format("%-15s %-15s %-15s %-15s %n", counter, kategoriTerkait.get(i), listArtikel[j].getId() , listArtikel[j].getTitle()));
//						out.newLine();
//						out.close();
						
						//-------------------------//
						
						System.out.println("	-Artikel dengan id " +listArtikel[j].getTitle()+ " telah di diextract boxnya");
			           }
					System.out.println("* Kategori '" +  kategoriTerkait.get(i) + "' - ekstraksi selesai");
		        }
				
				kategoriTerkait.clear();
		}
	}
	
	//0 for location, 1 for person, and 2 for organization
	private String ambilTextAnnot(String ask, String desc, int statusAnnot) throws IOException{
		String query = ask;
		String hasil = null;
		Cleaner c = new Cleaner();
		
		if(desc != null){
			if(c.readyAnnotate(query, desc)){
				
				switch(statusAnnot){
				case 0:
					hasil = c.setAnnotate(query, desc);
					break;
				case 1:
					hasil = c.setAnnotatePersOrganCountry(query, desc);
					break;
				case 2:
					hasil = c.setAnnotatePersOrganCountry(query, desc);
					break;
				}
			}
		}
		
		return hasil;
	}
	
	public void setAnnotate(String s) throws Exception{
		_input = new BufferedReader(new InputStreamReader(System.in));
		Page p;
		List<String> kategoriTerkait = new ArrayList<String>();
		List<String> childTerkait = new ArrayList<String>();
		List<tempBox> label = null ;
		connect.connect();
		String termKategori = s;
		 
		
		
			int k = 0;
			Iterator<Page> iter = _wikipedia.getPageIterator(PageType.category);
			System.out.println("\nMohon tunggu~\n");
				for (k=0; k<66889; k++){
					Page p1 = iter.next();
					p=p1;
					String isi = ambilKategori(termKategori, p.getTitle(), p.getId());
					if (isi != null){
					kategoriTerkait.add(isi);
					if(k == 66889)
						((BufferedReader) iter).close();
					}
				}
				
				for (int i=0 ; i<kategoriTerkait.size() ; i++) {
					System.out.println("** Kategori '" +  kategoriTerkait.get(i) + "' - eksraksi dimulai");
					
					Category kategori = _wikipedia.getCategoryByTitle(kategoriTerkait.get(i));
					Category[] listChild = kategori.getChildCategories();
					Article[] listArtikel = kategori.getChildArticles();
					String hasil = "";
					
					/*
					 * to handle if some category have child category
					 */
					if (listChild.length != 0){
						for (int l=0 ; l<listChild.length ; l++) {
							childTerkait.add(listChild[l].getTitle());
						}
					}

					try{
					for (int j=0 ; j<listArtikel.length ; j++) { 
						System.out.println("	- "+listArtikel[j].getId()+"- Artikel '" +  listArtikel[j].getTitle() + "' - DIEKSTRAK");
						
						
						Article[] setLinkOutArtikel = listArtikel[j].getLinksOut();
						
						String penampung = null;
				        List<tempBox> labelOut = null;
				        String akhir = c.getDesc(listArtikel[j]);
				        
				        label = c.getBox2(listArtikel[j]);
				        
				        // section below is to determine what type an article itself by label in it's box
				        for (int b = 0; b<label.size(); b++){
	 	            		 //System.out.print(labelOut.size()+"\n");
				        	for (int a = 0; a<label.get(b).getLabel().size(); a++){
				        		if (label.get(b).getLabel().get(a).equals("location")){
				        			if(!label.get(b).getValue().get(a).isEmpty()){
				        				penampung = ambilTextAnnot(label.get(b).getValue().get(a), akhir, 0);
				        				if(penampung != null)
		 	                        		 akhir = penampung;	
				        			} 
	 	                        	// System.out.print(listArtikel[j].getTitle()+ " memiliki label yang dicari\n");
				        		}else if (label.get(b).getLabel().get(a).equals("birth_date")||label.get(b).getLabel().get(a).equals("birthdate")||label.get(b).getLabel().get(a).equals("occupation")||label.get(b).getLabel().get(a).equals("religion")){
				        			penampung = ambilTextAnnot(listArtikel[j].getTitle(), akhir, 1);
				        			if(penampung != null)
	 	                        		 akhir = penampung;	
				        		}else if(label.get(b).getLabel().get(a).equals("founded")||label.get(b).getLabel().get(a).equals("foundation")){
				        			penampung = ambilTextAnnot(listArtikel[j].getTitle(), akhir, 2);
				        			if(penampung != null)
	 	                        		 akhir = penampung;	
				        			
				        		}
				        	}
	 	            	 }
				        
				        // check for certain label in linkOut article's box
				        
				        if (setLinkOutArtikel.length == 0) {
				       	   System.out.println("\n!! Kategori "+kategoriTerkait.get(i)+" tidak mempunyai link out artikel !!");
				          } else {
				       	   //System.out.println("=== link out ditemukan dan sedang di proses anotasi train nya : ===");
				              for (int m=0 ; m<setLinkOutArtikel.length ; m++) { 
				             	 //System.out.println(" - [" + (m+1) + "] " + setLinkOutArtikel[m].getTitle()+ "\n") ;
				             	 
				            	// check if it's person or not
	
					             	 
			             	 // check if in article box have certain label for determined if it's person or not
			             	 /// start from article it self. as for now we only search for label (birth_date, birthdate, occupation, religion)
			             		if(c.isPerson(setLinkOutArtikel[m])){
				             		 
				             		 penampung = ambilTextAnnot(setLinkOutArtikel[m].getTitle(), akhir, 2);
				             		 if(penampung != null)
				             			akhir = penampung;
			             		}
			             		
			             		// check if it's organization or not
			             		/// start from article it self. as for now we only search for label (founded, foundation, industry)
			             		if(c.isOrganization(setLinkOutArtikel[m])){
				             		 
				             		 penampung = ambilTextAnnot(setLinkOutArtikel[m].getTitle(), akhir, 2);
				             		 if(penampung != null)
				             			akhir = penampung;
			             		}

				             	 // check if it's country or not
				             	 if(c.isCountry(setLinkOutArtikel[m])){
				             		 
				             		 penampung = ambilTextAnnot(setLinkOutArtikel[m].getTitle(), akhir, 2);
				             		 if(penampung != null)
				             			akhir = penampung;
				             		 	//System.out.println(setLinkOutArtikel[m].getTitle()+ " merupakan negara\n"); 
				                	 
				                 //else linkOut wasn't a country, then check for it's header article and infobox. is there any certain label
				                 	 
				             	 }else if(c.containHeaderAnnot(setLinkOutArtikel[m])){
				             		 penampung = ambilTextAnnot(setLinkOutArtikel[m].getTitle(), akhir, 0);
				             		 if(penampung != null)
				             			akhir = penampung;
				             		 	//System.out.println(setLinkOutArtikel[m].getTitle()+ " terdapat header yang dicari\n");
				             		 	
				             	 }else{
				             	 
				 	            	 labelOut = c.getBox2(setLinkOutArtikel[m]);
				 	            	 
				 	            	 for (int x = 0; x<labelOut.size(); x++){
				 	            		 //System.out.print(labelOut.size()+"\n");
				 	            		 	            		 
				 	            			 if (labelOut.get(x).getLabel().contains("location")||labelOut.get(x).getLabel().contains("capital")||labelOut.get(x).getLabel().contains("penduduk")||labelOut.get(x).getLabel().contains("region")||labelOut.get(x).getLabel().contains("luas")||labelOut.get(x).getLabel().contains("peta")){
				 	            				 
				 	            				 penampung = ambilTextAnnot(setLinkOutArtikel[m].getTitle(), akhir, 0);
				 	                        	 if(penampung != null)
				 	                        		 akhir = penampung;		 
				 	                        	 //System.out.print(setLinkOutArtikel[m].getTitle()+ " memiliki label yang dicari\n");
				 	            			 }
				 	            			 
				 	            		 for (int c = 0; k<labelOut.get(x).getLabel().size(); c++){ 
				 	            			 
				 	            			 Pattern a = Pattern.compile("population_[a-z]+|population");
				 	            			 Pattern b = Pattern.compile("area_[a-z]+|area");
				 	            		     Matcher o = a.matcher(labelOut.get(j).getLabel().get(c));
				 	            		     Matcher n = b.matcher(labelOut.get(j).getLabel().get(c));
				 	            		     
				 	            		     if(o.find()||n.find()){
				 	            		    	 penampung = ambilTextAnnot(setLinkOutArtikel[m].getTitle(), akhir, 0);
				 	                        	 if(penampung != null)
				 	                        		 akhir = penampung;		 
				 	                        	 //System.out.print(setLinkOutArtikel[m].getTitle()+ " memiliki label yang dicari\n");
				 	            		     }
				 	            		     
				 	            			 
				 	            		 }
				 		 
				 	            	 }
				             	 }
				             	  
				             	 
				              }
				              
				          }
				        
				         akhir = c.lastCleanAnnot(akhir); 
				        
				         String[] sentence = akhir.split("(?<=[.!?])\\s* ");
				         Pattern r = Pattern.compile("<START:entity>");
				         
				         for (String z : sentence) {
				         	Matcher v = r.matcher(z);
				         	
				         	if(v.find()){
				         		try{
				         		out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/annotasi/labelFix/FULL.txt", true));
				         		out.append(z+"\n");
				         		out.close();
				         		}catch(IOException ie){
				         			
				         		}
				         	}
				         }

				         
					delay(10);
						
					}
					}catch(StackOverflowError soe){
						System.err.println(soe);
					}
					
					System.out.println("* Kategori '" +  kategoriTerkait.get(i) + "' - ekstraksi selesai");
				
				}
				
				// for child article from child category
				
				for (int i=0 ; i<childTerkait.size() ; i++) {
					System.out.println("** Kategori '" +  childTerkait.get(i) + "' - eksraksi dimulai");
					
					Category kategori = _wikipedia.getCategoryByTitle(childTerkait.get(i));
					Category[] listChild = kategori.getChildCategories();
					Article[] listArtikel = kategori.getChildArticles();
					String hasil = "";

					try{
					for (int j=0 ; j<listArtikel.length ; j++) { 
						System.out.println("	- "+listArtikel[j].getId()+"- Artikel '" +  listArtikel[j].getTitle() + "' - DIEKSTRAK");
						
						
						Article[] setLinkOutArtikel = listArtikel[j].getLinksOut();
						
						String penampung = null;
				        List<tempBox> labelOut = null;
				        String akhir = c.getDesc(listArtikel[j]);
				        
				        label = c.getBox2(listArtikel[j]);
				        
				        for (int b = 0; b<label.size(); b++){
	 	            		 //System.out.print(labelOut.size()+"\n");
				        	for (int a = 0; a<label.get(b).getLabel().size(); a++){
				        		if (label.get(b).getLabel().get(a).equals("location")){
				        			if(!label.get(b).getValue().get(a).isEmpty()){
				        				penampung = ambilTextAnnot(label.get(b).getValue().get(a), akhir, 0);
				        			}
	 	                        	 if(penampung != null)
	 	                        		 akhir = penampung;		 
	 	                        	// System.out.print(listArtikel[j].getTitle()+ " memiliki label yang dicari\n");
				        		}else if (label.get(b).getLabel().get(a).equals("birth_date")||label.get(b).getLabel().get(a).equals("birthdate")||label.get(b).getLabel().get(a).equals("occupation")||label.get(b).getLabel().get(a).equals("religion")){
				        			penampung = ambilTextAnnot(listArtikel[j].getTitle(), akhir, 1);
				        			if(penampung != null)
	 	                        		 akhir = penampung;	
				        		}else if(label.get(b).getLabel().get(a).equals("founded")||label.get(b).getLabel().get(a).equals("foundation")){
				        			penampung = ambilTextAnnot(listArtikel[j].getTitle(), akhir, 2);
				        			if(penampung != null)
	 	                        		 akhir = penampung;	
				        			
				        		}		
				        	}
	 	            	 }
				        
				        if (setLinkOutArtikel.length == 0) {
				       	   System.out.println("\n!! Kategori "+childTerkait.get(i)+" tidak mempunyai link out artikel !!");
				          } else {
				       	   //System.out.println("=== link out ditemukan dan sedang di proses anotasi train nya : ===");
				              for (int m=0 ; m<setLinkOutArtikel.length ; m++) { 
				             	 //System.out.println(" - [" + (m+1) + "] " + setLinkOutArtikel[m].getTitle()+ "\n") ;
				             	 
				            	// check if in article box have certain label for determined if it's person or not
					             	 /// start from article it self. as for now we only search for label (birth_date, birthdate, occupation, religion)
					             		if(c.isPerson(setLinkOutArtikel[m])){
						             		 
						             		 penampung = ambilTextAnnot(setLinkOutArtikel[m].getTitle(), akhir, 1);
						             		 if(penampung != null)
						             			akhir = penampung;
					             		}
					             		
					             		// check if it's organization or not
					             		/// start from article it self. as for now we only search for label (founded, foundation, industry)
					             		if(c.isOrganization(setLinkOutArtikel[m])){
						             		 
						             		 penampung = ambilTextAnnot(setLinkOutArtikel[m].getTitle(), akhir, 2);
						             		 if(penampung != null)
						             			akhir = penampung;
					             		}
				            	  
				             	 // check apakah link out tersebut adalah negara atau bukan
				             	 if(c.isCountry(setLinkOutArtikel[m])){
				             		 
				             		 penampung = ambilTextAnnot(setLinkOutArtikel[m].getTitle(), akhir, 0);
				             		 if(penampung != null)
				             			akhir = penampung;
				             		 	//System.out.println(setLinkOutArtikel[m].getTitle()+ " merupakan negara\n");
				                 		 
				                 //else linkOut wasn't a country, then check for it's infobox. if there any label named  location
				                 	 
				             	 }else if(c.containHeaderAnnot(setLinkOutArtikel[m])){
				             		 penampung = ambilTextAnnot(setLinkOutArtikel[m].getTitle(), akhir, 0);
				             		 if(penampung != null)
				             			akhir = penampung;
				             		 	//System.out.println(setLinkOutArtikel[m].getTitle()+ " terdapat header yang dicari\n");
				             		 	
				             	 }else{
				             	 
				 	            	 labelOut = c.getBox2(setLinkOutArtikel[m]);
				 	            	 
				 	            	 for (int x = 0; x<labelOut.size(); x++){
				 	            		 //System.out.print(labelOut.size()+"\n");
				 	            		 	            		 
				 	            			 if (labelOut.get(x).getLabel().contains("location")||labelOut.get(x).getLabel().contains("capital")||labelOut.get(x).getLabel().contains("penduduk")||labelOut.get(x).getLabel().contains("region")||labelOut.get(x).getLabel().contains("luas")||labelOut.get(x).getLabel().contains("peta")){
				 	            				 
				 	            				 penampung = ambilTextAnnot(setLinkOutArtikel[m].getTitle(), akhir, 0);
				 	                        	 if(penampung != null)
				 	                        		 akhir = penampung;		 
				 	                        	 //System.out.print(setLinkOutArtikel[m].getTitle()+ " memiliki label yang dicari\n");
				 	            			 }
				 	            			 
				 	            		 for (int c = 0; k<labelOut.get(x).getLabel().size(); c++){ 
				 	            			 
				 	            			 Pattern a = Pattern.compile("population_[a-z]+|population");
				 	            			 Pattern b = Pattern.compile("area_[a-z]+|area");
				 	            		     Matcher o = a.matcher(labelOut.get(j).getLabel().get(c));
				 	            		     Matcher n = b.matcher(labelOut.get(j).getLabel().get(c));
				 	            		     
				 	            		     if(o.find()||n.find()){
				 	            		    	 penampung = ambilTextAnnot(setLinkOutArtikel[m].getTitle(), akhir, 0);
				 	                        	 if(penampung != null)
				 	                        		 akhir = penampung;		 
				 	                        	 //System.out.print(setLinkOutArtikel[m].getTitle()+ " memiliki label yang dicari\n");
				 	            		     }
				 	            		     
				 	            			 
				 	            		 }
				 		 
				 	            	 }
				             	 }
				             	 
				             	 
				              }
				              
				          }
				         
				         akhir = c.lastCleanAnnot(akhir); 
				         String[] sentence = akhir.split("(?<=[.!?])\\s* ");
				         Pattern r = Pattern.compile("<START:entity>");
				         
				         for (String z : sentence) {
				         	Matcher v = r.matcher(z);
				         	
				         	if(v.find()){
				         		try{
				         		out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/annotasi/labelFix/FULL.txt", true));
				         		out.append(z+" ");
				         		out.close();
				         		}catch(IOException ie){
				         			
				         		}
				         	}
				         }

				         
					delay(10);
						
					}
					}catch(StackOverflowError soe){
						System.err.println(soe);
					}
					
					System.out.println("* Kategori '" +  childTerkait.get(i) + "' - ekstraksi selesai");
				
				}
				
				kategoriTerkait.clear();
				childTerkait.clear();
		}
	
	public void repairAnnot(){
		String tempText = null;
		Pattern p = Pattern.compile("(.*?\\.[ \n](?!(?:\\w+\\s<END>)))");
		Pattern q = Pattern.compile("<START:entity>");
		String sentence = null;
		
		try{
		_input = new BufferedReader(new FileReader("X:/Database Berkeley/Output/annotasi/result/works.txt"));
		}catch(FileNotFoundException fnfe){
			System.err.println("FILE TIDAK DITEMUKAN \n");
		}
		
		try {
			while((tempText = _input.readLine()) != null){
				
				Matcher m = p.matcher(tempText);
				
				while(m.find()){
					sentence = m.group(1);
					Matcher n = q.matcher(sentence);
					
					if(n.find()){
						try{
			         		out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/annotasi/result/NEWFULL.txt", true));
			         		out.append(sentence+"\n");
			         		out.close();
			         		}catch(IOException ie){
			         			
			         		}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

    public static void main(String args[]) throws Exception {
    	WikipediaConfiguration conf = new WikipediaConfiguration(new File("X:/Database Berkeley/wikipedia-miner-1.2.0/wikipedia-template.xml"));
    	Wikicorpus korpus = new Wikicorpus(conf);
    	//korpus.ArticleSet((new File("X:/Database Berkeley/datadir/pageId.csv")), _wikipedia);
    	
    	//korpus.docer();
    	
    	//korpus.deskriptor();
    	//korpus.repairAnnot();
    	//korpus.getArticleByCategory();
    	korpus.setAnnotate("dalam tahun");
//    	korpus.inputToDatabase("Yahudi", "Mongolia");
//    	korpus.inputToDatabase("Mongolia", "Bumi");
//    	korpus.inputToDatabase("Bumi", "Nasal, Kaur");
//    	korpus.inputToDatabase("Nasal, Kaur", "Galaksi");
//    	korpus.inputToDatabase("Galaksi", "Dasawarsa");
//    	korpus.inputToDatabase("Dasawarsa", "Gambar lambang");
//    	korpus.inputToDatabase("Gambar lambang", "Buruh");
//    	korpus.inputToDatabase("Buruh", "Geografi Oman");
//    	korpus.inputToDatabase("Geografi Oman", "Tokoh dari Kuningan");
//    	korpus.inputToDatabase("Tokoh dari Kuningan", "Region di Italia");
//    	korpus.inputToDatabase("Region di Italia", "Melbourne");
//    	korpus.inputToDatabase("Melbourne", "Tokoh Flandria");
//    	korpus.inputToDatabase("Tokoh Flandria", "Penguasa monarki");
//    	korpus.inputToDatabase("Penguasa monarki", "Wahyu kepada Yohanes");
//    	korpus.inputToDatabase("Wahyu kepada Yohanes", "Kelahiran 847");
//    	korpus.inputToDatabase("Kelahiran 847", "Kue Jepang");
//    	korpus.inputToDatabase("Kue Jepang", "Kerajaan Blambangan");
//    	korpus.inputToDatabase("Kerajaan Blambangan", "Penyamak kulit");
//    	korpus.inputToDatabase("Penyamak kulit", "Kontes kecantikan di Britania Raya");
//    	korpus.inputToDatabase("Kontes kecantikan di Britania Raya", "Meja");
//    	korpus.inputToDatabase("Meja", "Maret 2015");
//    	korpus.inputToDatabase("Maret 2015", "Pengusaha Sunda");
//    	korpus.inputToDatabase("Pengusaha Sunda", "Myxini");
//    	korpus.inputToDatabase("Myxini", "Nobel Ekonomi");
//    	korpus.inputToDatabase("Nobel Ekonomi", "Coimbra");
//    	korpus.inputToDatabase("Coimbra", "Kejadian 1");
//    	korpus.inputToDatabase("Kejadian 1", "Yesaya 22");
    	
    	//korpus.getDescFromArticle();
    	/*
    	 * membuat corpus dari seluruh artikel wikipedia
    	 */
    	//korpus.corpusClean();
    	
    }

   

	
}