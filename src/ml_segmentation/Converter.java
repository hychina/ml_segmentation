package ml_segmentation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class Converter {

	public static void main(String[] args) throws IOException {
		
//		write_file("crf_train_trad.data", train2tokens(read_file("data/trad/train_trad.data", "utf-16")), "utf-8");
//		write_file("crf_test_trad.data", test2tokens(read_file("data/trad/test_trad.data", "utf-16")), "utf-8");
//		write_file("result_test_trad.txt", tokens2words("crf_result_test_trad.data"), "utf-16le");
		
//		write_file("valid_trad/crf_train.data", train2tokens(read_file("valid_trad/train.txt", "utf-8")), "utf-8");
//		write_file("valid_trad/crf_test.data", train2tokens(read_file("valid_trad/test.txt", "utf-8")), "utf-8");
		
//		write_file("valid_trad/result_correct.txt", tokens2words("valid_trad/crf_result.data", 1), "utf-8");
//		write_file("valid_trad/result_crf.txt", tokens2words("valid_trad/crf_result.data", 2), "utf-8");
		
		// 2. 简体
		
		// 2.1 对验证数据进行分词
		
		// 2.1.1 准备 tokens
//		write_file("valid_simp/crf_train.data", train2tokens(read_file("valid_simp/train.txt", "utf-8")), "utf-8");
//		write_file("valid_simp/crf_test.data", train2tokens(read_file("valid_simp/test.txt", "utf-8")), "utf-8");
		
		// 2.1.2 生成分词结果
//		write_file("valid_simp/result_correct.txt", tokens2words("valid_simp/crf_result.data", 1), "utf-8");
//		write_file("valid_simp/result_crf.txt", tokens2words("valid_simp/crf_result.data", 2), "utf-8");
		
		// 2.2 对测试数据进行分词
		
		// 2.2.1 准备 tokens
//		write_file("test_simp/crf_train_simp.data", train2tokens(read_file("data/simp/train_simp.data", "utf-8")), "utf-8");
//		write_file("test_simp/crf_test_simp.data", test2tokens(read_file("data/simp/test_simp.data", "utf-8")), "utf-8");
		
		// 2.2.2 生成分词结果
		write_file("test_simp/result_simp.txt", tokens2words("test_simp/crf_result_simp.data", 2), "utf-8");
	}
	
	// �� crf++ ��ɵĽ��תΪ�ո�ֿ��ĵ���
	private static String tokens2words(String filename, int ans_index) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename), Charset.forName("utf-8")));
		
		StringBuilder sb = new StringBuilder();
		String s;
		boolean is_begin_of_line = true;
		
		while ((s = in.readLine()) != null) {
			if (s.equals("")) {
				sb.append('\n');
				is_begin_of_line = true;
			} else {
				String[] splits = s.split("\t");
				String tag = splits[ans_index];
							
				if (tag.equals("B") && !is_begin_of_line) {
					sb.append("  ");
				}
				sb.append(splits[0]);
				is_begin_of_line = false;
			}
		}
		in.close();
		
		return sb.toString();
	}
	
	// ��������תΪ tokens
	private static String test2tokens(String input) {
		StringBuilder sb = new StringBuilder();
		char[] cs = input.toCharArray();
		
		for (int i = 0; i < cs.length; ++i) {
			sb.append(cs[i]);
			if (cs[i] != '\n')
				sb.append("\tB\n");
		}
		return sb.toString();
	}
	
	// ѵ��Ԥ��תΪ tokens
	private static String train2tokens(String input) {
		StringBuilder sb = new StringBuilder();
		
		char[] cs = input.toCharArray();
		int word_begin = 0;
		//int word_begin = 0;
		
		for (int i = 0; i < cs.length; ++i) {
			if (cs[i] == ' ') { // ���ո���ȡ���ʣ�תΪtoken
				
				sb.append(cs[word_begin]);
				sb.append("\tB\n");
				for(int j = word_begin + 1; j < i; ++j) {
					sb.append(cs[j]);
					sb.append("\tI\n");
				}
				
				while (true) {
					++i;
					if (i == cs.length) break;
					if (cs[i] != ' ') {
						if (cs[i] == '\n') {
							sb.append('\n');
						} 
						
						else break;
					}
				}					
				
				word_begin = i;
			}
		}
		
		return sb.toString();
	}
	
	public static String read_file(String input_file, String input_encoding) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(input_file), Charset.forName(input_encoding)));
		
		StringBuilder sb = new StringBuilder();
		String s;
		
		while ((s = in.readLine()) != null) {
//			if (s.equals(""))
//				sb.append("\n");
//			else 
//				sb.append(s);
			sb.append(s + '\n');
		}
		in.close();
		
		return sb.toString();
	}
	
	public static void write_file(String output_file, String data, String output_encoding) throws IOException {
		PrintWriter fos = new PrintWriter( new BufferedWriter( new OutputStreamWriter( 
				new FileOutputStream(output_file), Charset.forName(output_encoding))));
		
		if (output_encoding.equals("utf-16le")) {
			StringBuilder sb = new StringBuilder();
			sb.append("\uFEFF");
			sb.append(data);
			fos.write(sb.toString());
		} else {
			fos.write(data);
		}
			
		fos.close();
	}

}
