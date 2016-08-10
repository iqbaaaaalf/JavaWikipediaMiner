import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.util.MarkupStripper;

import com.google.common.base.CharMatcher;

public class Cleaner {
	
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
		Pattern p = Pattern.compile("(Infobox.*\n(?:|.*\n)+)}}");
		Matcher m = p.matcher(markupDirt);
		String markupClean = null;
		if (m.find())
		{
			markupClean = m.group(1);
			markupClean = CharMatcher.anyOf(charsToRemove).replaceFrom(markupClean, " ");
			markupClean = CharMatcher.anyOf(CharsToRemove1).replaceFrom(markupClean, "");
			//menghilangan tag dan isi dalam tag
			markupClean = markupClean.replaceAll("(?s)<small\\s(.*?)>(.*?)</small>", " ");
			// menghapus tag <br  />
			markupClean = markupClean.replaceAll("<br />|<br>", "");
			// menghapus semua ref tag (atribut) dengan contentnya
			markupClean = markupClean.replaceAll("(?s)<ref\\s(.*?)>(.*?)</ref>", " ");
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
			
			
		}
		else
			markupClean = "";
		
//		markupClean = CharMatcher.anyOf(charsToRemove).replaceFrom(markupClean, " ");
//		markupClean = markupClean.replaceAll("[\\[\\]\\{\\}]", "");
		//String markupClean = markupDirt;
		return markupClean;
	}
	
	public String getDesc(Page isi){
		MarkupStripper stripper = new MarkupStripper() ;
		String markupDirt = isi.getMarkup();
		String markupClean = "";
		
		markupClean = markupDirt.replaceAll("={2,}(.+)={2,}", "\n") ; //clear section headings completely - not just formating, but content as well.			
		markupClean = stripper.stripAllButInternalLinksAndEmphasis(markupClean, null) ;
		markupClean = stripper.stripNonArticleInternalLinks(markupClean, null) ;
		markupClean = stripper.stripExcessNewlines(markupClean) ;

		String fp = "" ;
		int pos = markupClean.indexOf("\n\n") ;

		while (pos>=0) {
			fp = markupClean.substring(0, pos) ;

			if (pos > 100000) 
				break ;

			pos = markupClean.indexOf("\n\n", pos+2) ;
		}

		fp = fp.replaceAll("\n", " ") ;
		fp = fp.replaceAll("\\s+", " ") ;  //turn all whitespace into spaces, and collapse them.
		fp = fp.trim();
		
		
		return fp;
	}
	

	public Boolean isKategori(String kategori, String query){
		Pattern p = Pattern.compile("(?! )("+query+")(?: )", Pattern.CASE_INSENSITIVE);
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
