package com.arsnindo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.util.MarkupStripper;

import com.google.common.base.CharMatcher;

public class Cleaner {
		
	Connect connect = new Connect();
	
	public String getSentenceClean(Page isi) throws Exception{
		String markupDirt = isi.getMarkup(); 
		
		//using guava //
		//inisiasi char apa yang akan disamakan pada suatu text
		String charsToRemove = "*!|:";
		String CharsToRemove1 = "[]'" ; 
		//mengahapus masing masing symbol dari  charsToRemove pada String markup
		String markupDirt1 = CharMatcher.anyOf(charsToRemove).replaceFrom(markupDirt, " ");
		//mengahapus masing masing symbol dari  charsToRemove1 pada String markup
		String markupClean = CharMatcher.anyOf(CharsToRemove1).replaceFrom(markupDirt1, "");
		//menghapus tag dan content pada tag <ref> sampai close tag nya
		//using guava//
		
		// menghapus semua gallery tag (atribut) dengan contentnya
		markupClean = markupClean.replaceAll("(?s)<gallery>.*?</gallery>", "");
		// menghapus semua ref tag (atribut) dengan contentnya
		markupClean = markupClean.replaceAll("(?s)<ref\\s(.*?)>(.*?)</ref>", " ");
		// menghapus semua external link
		markupClean = markupClean.replaceAll("\\[(http|www)(.*?)\\]", " ");
		// menghapus semua html tag yang tersisa
		markupClean = markupClean.replaceAll("<(.*?)>", " ");
		// menghapus markup untuk bold dan italic
		markupClean = markupClean.replaceAll("'{2,}", " ");
		// menghilangkan tab menjorong kedalam
		markupClean = markupClean.replaceAll("\n:+", " ");
		// menghapus penanda list
		markupClean = markupClean.replaceAll("\n([\\*\\#]+)", " ");
		//1 newline diganti 2
		//markupClean = markupClean.replaceAll("\\n", "\n\n");
		// membersihkan tanda punctuation
		markupClean = markupClean.replaceAll("[\\[\\]\\{\\}]", "");
		// membersihkan penanda '===' header
		markupClean = markupClean.replaceAll("===", "");
		markupClean = markupClean.replaceAll("==", "");
		markupClean = markupClean.replaceAll("&nbsp;", " ");
		markupClean.trim();
		return markupClean ;
	}
	
	public String getBox(Page isi){
		String markupDirt = isi.getMarkup();
		String charsToRemove = "*!:|";
		String CharsToRemove1 = "'" ; 
		markupDirt = markupDirt.replaceAll("<ref.*>.*\n.*<\\/ref>", "");//<ref\s(.*?)>(.*?)<\/ref>
		markupDirt = markupDirt.replaceAll("<ref\\s(.*?)>(.*?)</ref>", ""); //<ref>(.*?)<\/ref>
		markupDirt = markupDirt.replaceAll("<ref>(.*?)</ref>", ""); //<ref>((.*?)(\n.*?)?)<\/ref>
		markupDirt = markupDirt.replaceAll("<ref>((.*?)(\n.*?)?)</ref>", "");
		
		Pattern p = Pattern.compile("\\{\\{(.*)\n(\\|.*\n)+\\}\\}"); //for get box
		//Pattern p = Pattern.compile("\\{\\{\\w.+\n(\\|(.*\n)+)\\}\\}?");
		//Pattern p = Pattern.compile("\\{\\{\\w.+\n((\\|.*\n)+)\\}\\}?");
		String markupClean = null;
		
		try{
		Matcher m = p.matcher(markupDirt);
		if (m.find())
		{
			markupClean = m.group(1);
			markupClean = CharMatcher.anyOf(charsToRemove).replaceFrom(markupClean, " ");
			markupClean = CharMatcher.anyOf(CharsToRemove1).replaceFrom(markupClean, "");
			//menghilangan tag dan isi dalam tag
			markupClean = markupClean.replaceAll("(?s)<small\\s(.*?)>(.*?)</small>", " ");
			markupClean = CharMatcher.anyOf(charsToRemove).replaceFrom(markupClean, " ");
			// menghapus tag <br  />
			markupClean = markupClean.replaceAll("<br />|<br>", "");
			// menghapus semua ref tag (atribut) dengan contentnya
			//menghilangkan tanda < spanberserta stylenya
			markupClean = markupClean.replaceAll("(?s)<span\\s(.*?)>(.*?)</span>", " $2  ");
			// menghilangkan tag sub 
			markupClean = markupClean.replaceAll("(?s)<sub>(.*?)</sub>", "");
			// menghapus tanda <sup> yang menandakan pangkat
			markupClean = markupClean.replaceAll("<sup>", "^");
			// untuk mengganti tanda ?didepan angka yangseharusnya adalah -
			// mengganti tanda <sup> dengan space
			markupClean = markupClean.replaceAll("</sup>", " ");
			//menghapus tanda [ ] ( )
			markupClean = markupClean.replaceAll("[\\(\\)\\[\\]]", "");
			// menghapus tanda dan isi diantara bracket (?) mengatasi native title dari 'bahasa' yang tidak dapat esktrak(eg: bahasa indonesia, bahasa jawa)
			markupClean = markupClean.replaceAll("(\\{\\{.*}})", "");
			//menambahkan whitespace setelah '='
			markupClean = markupClean.replaceAll("=(?=.)", "$0 ");
			// menghilangkan tanda tanya berlebih
			markupClean = markupClean.replaceAll("[\\?]", "");
			//menghilangkan tanda &nbsp hasil ekstraksi suatu simbol
			markupClean = markupClean.replaceAll("&nbsp;", " ");
			markupClean = markupClean.replaceAll("\\?(\\d)", "-$2");
			markupClean = markupClean.replaceAll("\\{\\{|\\}\\}", "");
			
			
		}
		else
			markupClean = "";
		}catch (NullPointerException e){
			System.err.println("File tidak mempunyai markup pada dataabse");
		}
		
//		markupClean = CharMatcher.anyOf(charsToRemove).replaceFrom(markupClean, " ");
//		markupClean = markupClean.replaceAll("[\\[\\]\\{\\}]", "");
		//String markupClean = markupDirt;
		return markupClean;
	}
	
	public List<tempBox> getBox2(Page isi){
		String markupDirt = isi.getMarkup();
		String boxCategory = null;
		String charsToRemove = "*!:|";
		String CharsToRemove1 = "'" ;
		int count =0;
		int boxType= 0;
		List<String> label = new ArrayList<String>();
		List<String> value = new ArrayList<String>();
		List<tempBox> listBox = new ArrayList<tempBox>();
		String valueString = null;
		String labelString = null;
		
		//handle berbagai macam pola referensi dan kemudian dibuang
		try{
		markupDirt = markupDirt.replaceAll("<ref.*>.*\n.*<\\/ref>", "");//<ref\s(.*?)>(.*?)<\/ref>
		markupDirt = markupDirt.replaceAll("<ref\\s(.*?)>(.*?)</ref>", ""); //<ref>(.*?)<\/ref>
		markupDirt = markupDirt.replaceAll("<ref>(.*?)</ref>", ""); //<ref>((.*?)(\n.*?)?)<\/ref>
		markupDirt = markupDirt.replaceAll("<ref>((.*?)(\n.*?)?)</ref>", "");
		//Pattern p = Pattern.compile("\\{\\{\\w.+\n((\\|.*\n)+)\\}\\}"); //for get box
//		Pattern q = Pattern.compile("\\{\\{(.*\n)(\\|.*\n)+\\}\\}"); //for get box category (group 1){{(\w+\s\w+\n)(\|.*\n)+}}
//		Pattern q = Pattern.compile("\\{\\{(\\w+\\s\\w+\n)(\\|.*\n)+\\}\\}"); {{(.*)\n(\|.*\n)+}}
		}catch (ClassCastException ce){
			
		}catch(NullPointerException en){
			
		}catch(IllegalArgumentException ie){
			
		}
		
		Pattern q = Pattern.compile("\\{\\{(.*)\n(\\|.*\n)+\\}\\}"); //for get box
		Pattern r = Pattern.compile("(?!\\{\\{.*\n)\\|(.*)=(.*\n)"); //for get label (key) and its value
		String markupClean = null;
		
		try{
		Matcher m = q.matcher(markupDirt);
		
		markupClean = markupDirt.replaceAll("\\{\\{cite.*\n(\\|(.*\n)*)\\}\\}", null);
		while (m.find())
		{
			count++;
			markupClean = m.group(0);
			boxCategory = m.group(1);
			//Matcher n = q.matcher(markupClean);
//			while (n.find()){
//				boxCategory = n.group(1);
//			}
			
			Matcher o = r.matcher(markupClean);
			try{
				while (o.find()){
					String labelAdd = o.group(1).replaceAll("\\s+", "");
					label.add(labelAdd);
					valueString = o.group(2).replaceAll("[\\(\\)\\[\\]]", "");
					//menghilangan tag dan isi dalam tag
					valueString = valueString.replaceAll("(?s)<small\\s(.*?)>(.*?)</small>", " ");
					// menghapus tag <br  />
					valueString = valueString.replaceAll("<br />|<br>", "");
					// menghapus semua ref tag (atribut) dengan contentnya
					valueString = valueString.replaceAll("(?s)<ref\\s(.*?)>(.*?)</ref>", " ");
					//menghilangkan tanda < spanberserta stylenya
					valueString = valueString.replaceAll("(?s)<span\\s(.*?)>(.*?)</span>", " $2  ");
					// menghilangkan tag sub 
					valueString = valueString.replaceAll("(?s)<sub>(.*?)</sub>", "");
					// menghapus tanda <sup> yang menandakan pangkat
					valueString = valueString.replaceAll("<sup>", "^");
					// untuk mengganti tanda ?didepan angka yangseharusnya adalah -
					// mengganti tanda <sup> dengan space
					valueString = valueString.replaceAll("</sup>", " ");
					// menghapus tanda dan isi diantara bracket (?) mengatasi native title dari 'bahasa' yang tidak dapat esktrak(eg: bahasa indonesia, bahasa jawa)
					valueString = valueString.replaceAll("(\\{\\{.*}})", "");
					//menghilangkan tanda &nbsp hasil ekstraksi suatu simbol
					valueString = valueString.replaceAll("&nbsp;", " ");
					valueString = valueString.replaceAll("\\?(\\d)", "-$1");
					valueString = valueString.replaceAll("\\{\\{|\\}\\}", "");
					value.add(valueString);
				}
			}catch (ClassCastException ce){
				
			}catch(NullPointerException en){
				
			}catch(IllegalArgumentException ie){
				
			}
			
			if(count == 1)
				boxType = 1;
			else if (count > 1)
				boxType = 2;
				
			tempBox temp = new tempBox(boxCategory, label, value, boxType);
			try{
			listBox.add(temp);
			}catch (ClassCastException ce){
				
			}catch(NullPointerException en){
				
			}catch(IllegalArgumentException ie){
				
			}
		}
		
			}catch (NullPointerException e){
				System.err.println("Artikel tidak mempunyai box");
			}

		return listBox;
	}
	
	public boolean isCountry(Page isi){
		try{
		String markupDirt = isi.getMarkup();
		Pattern p = Pattern.compile("\\{\\{.*infobox\\}\\}");
		Matcher m = p.matcher(markupDirt);
		if(m.find()){
			return true;
		}
		}catch (NullPointerException e){
			System.err.println("File tidak mempunyai markup yang dicari pada database");
		}catch(StringIndexOutOfBoundsException se){
			System.err.println("Proses Cleaner bermasalah, file akan di skip");
		}
		return false;
	}
	
	public boolean isPerson(Page isi){
		try{
			List<tempBox> label = getBox2(isi);
			
			for (int x = 0; x<label.size(); x++){     		 
	    			 if (label.get(x).getLabel().contains("birth_date")||label.get(x).getLabel().contains("birthdate")||label.get(x).getLabel().contains("occupation")||label.get(x).getLabel().contains("religion")){
	    				 return true;
	    			 }
			}
		}catch (NullPointerException e){
			System.err.println("File tidak mempunyai markup yang dicari pada database");
		}catch(StringIndexOutOfBoundsException se){
			System.err.println("Proses Cleaner bermasalah, file akan di skip");
		}
		return false;
	}
	
	public boolean isOrganization(Page isi){
		try{
			List<tempBox> label = getBox2(isi);
			
			for (int x = 0; x<label.size(); x++){     		 
	    			 if (label.get(x).getLabel().contains("founded")||label.get(x).getLabel().contains("foundation")||label.get(x).getLabel().contains("industry")){
	    				 return true;
	    			 }
			}
		}catch (NullPointerException e){
			System.err.println("File tidak mempunyai markup yang dicari pada database");
		}catch(StringIndexOutOfBoundsException se){
			System.err.println("Proses Cleaner bermasalah, file akan di skip");
		}
		return false;
	}
	

	public String getDesc(Page isi){
		MarkupStripper stripper = new MarkupStripper() ;
		String markupDirt = isi.getMarkup();
		String markupClean = "";
		
		try{
		
		// remove header title //
		markupClean = markupDirt.replaceAll("={3,}(.+)={3,}", "") ;
		markupClean = markupClean.replaceAll("={2,}(.+)={2,}", "");
		markupClean = markupClean.replaceAll("={4,}(.+)={4,}", "");
		//
		
		//delete manual box without wikipedia miner function just in case it wasn't clearly delete box
		/// first of all, delete tag ref that disturb us to find template for box 
		markupClean = markupClean.replaceAll("<ref.*>.*\n.*<\\/ref>", "");//<ref\s(.*?)>(.*?)<\/ref>
		markupClean = markupClean.replaceAll("<ref\\s(.*?)>(.*?)</ref>", ""); //<ref>(.*?)<\/ref>
		markupClean = markupClean.replaceAll("<ref>(.*?)</ref>", ""); //<ref>((.*?)(\n.*?)?)<\/ref>
		markupClean = markupClean.replaceAll("<ref>((.*?)(\n.*?)?)</ref>", "");
		///
		///search box template end delete it
		markupClean = markupClean.replaceAll("\\{\\{.*\n(\\|.*\n)+\\}\\}", "");
		///
		//
		
		
		markupClean = stripper.stripAllButInternalLinksAndEmphasis(markupClean, null) ;
		markupClean = stripper.stripNonArticleInternalLinks(markupClean, null) ;
		markupClean = stripper.stripInternalLinks(markupClean, null);
		markupClean = stripper.stripExcessNewlines(markupClean) ;
		markupClean = markupClean.replaceAll("( Lihat pula \n)(.*\n)+", "");
		markupClean = markupClean.replaceAll("( Artikel terkait \n)(.*\n)+", "");
		markupClean = markupClean.replaceAll("(Referensi\n)(.*\n)+", "");
		markupClean = markupClean.replaceAll("( Lihat juga \n)(.*\n)+", "");
		markupClean = markupClean.replaceAll("( Pranala luar \n)(.*\n)+", "");
		markupClean = markupClean.replaceAll("''", "");
		markupClean = markupClean.replaceAll("&nbsp;", " ");
		markupClean = markupClean.replaceAll("-{1,}", "");
		markupClean = markupClean.replaceAll("\\|.*", "");
		markupClean = markupClean.replaceAll(".*\n([*]{1,}.*\n)+|([*]{1,}.*\n)+", "");
		markupClean = markupClean.replaceAll("Berkas:Stamps of .*", "");
		}catch (NullPointerException e){
			System.err.println("File tidak mempunyai markup yang dicari pada database");
		}catch(StringIndexOutOfBoundsException se){
			System.err.println("Proses Cleaner bermasalah, file akan di skip");
		}
		
		String fp = "" ;
		fp = markupClean;
		
		
		return fp;
	}
	
	public String lastCleanAnnot(String isi){
		String markupClean = isi;
		try{
		markupClean = markupClean.replaceAll("\\|.*", "");
		markupClean = markupClean.replaceAll(".*\n([*]{1,}.*\n)+|([*]{1,}.*\n)+", "");
		markupClean = markupClean.replaceAll("Berkas:.*", "");
		//markupClean = markupClean.replaceAll("(?:<START:entity> ){2,}(.*?)(?:<END>[,. ]){2,}", "<START:entity> $1 <END>");
		markupClean = markupClean.replaceAll("<START:entity>(\\w+.*)<END>", "<START:entity> $1<END>");
		markupClean = markupClean.replaceAll("<START:entity>(.*)<START:entity>(.*)<END>(.*)<END>", "$1<START:entity>$2<END>$3");
		}catch (NullPointerException e){
			System.err.println("File tidak mempunyai markup yang dicari pada database");
		}catch(StringIndexOutOfBoundsException se){
			System.err.println("Proses Cleaner bermasalah, file akan di skip");
		}
		return markupClean;
		
	}
	
	public Boolean readyAnnotate(String query, String desc){
		Pattern p = Pattern.compile("(?! )("+query+")(?! )|(?! )("+query+")|("+query+")(?: )", Pattern.CASE_INSENSITIVE);
		try{
		Matcher m = p.matcher(desc);
		if (m.find()){
			return true;
		}
		}catch (NullPointerException e){
			System.err.println("File tidak mempunyai markup pada database");
		}catch(StringIndexOutOfBoundsException se){
			System.err.println("Proses Cleaner bermasalah, file akan di skip");
		}
		
		return false;
	}
	
	public boolean containHeaderAnnot(Page isi){
		String markupDirt = isi.getMarkup();
		Pattern q = Pattern.compile("== Geografi ==|== Geografis ==|== Demografi ==|== Ekonomi ==|== Daerah ==");// regex can be modified to "={2,} SomethingWord ={2,}"
		try{
		Matcher m = q.matcher(markupDirt);
		
		if(m.find()){
			return true;
		}
		}catch (NullPointerException e){
			System.err.println("File tidak mempunyai markup yang dicari pada database");
		}catch(StringIndexOutOfBoundsException se){
			System.err.println("Proses Cleaner bermasalah, file akan di skip");
		}
		return false;
	}
	
	public String setAnnotate(String query, String desc){
		String hasil = null;
		
		hasil = desc.replaceAll("([dD]i|[dD]ari|[kK]e|[mM]enuju)(?:\\s(\\w+\\s)|(?: ))"+query, "$1 <START:entity>$2 "+query+" <END> ");
		// (?:[dD]i|[dD]ari|[kK]e|[mM]enuju)(?:\s(\w+\s)|(?: ))aceh(?:\s(\w+)|[.,!])
		return hasil;
	}
	
	public String setAnnotatePersOrganCountry(String query, String desc){
		String hasil = null;
		
		hasil = desc.replaceAll(query, "<START:entity> "+query+" <END> ");
		// (?:[dD]i|[dD]ari|[kK]e|[mM]enuju)(?:\s(\w+\s)|(?: ))aceh(?:\s(\w+)|[.,!])
		return hasil;
	}

	public Boolean isKategori(String kategori, String query){
		Pattern p = Pattern.compile("(?! )("+query+")(?: )|(?! )("+query+")|("+query+")(?: )", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(kategori);
		
		if (m.find()){
			return true;
		}
		
		return false;
	}
	
	
	
	public Boolean isKategoriFix(String kategori, String query){
		Pattern p = Pattern.compile(query);
		Matcher m = p.matcher(kategori);
		
		if (m.find()){
			return true;
		}
		
		return false;
	}
	
}
