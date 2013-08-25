package ml_segmentation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class Validation {

	private static int num_my_words;
	private static int num_std_words;
	private static int num_correct_words;
	
	private static final int new_line = "\n".codePointAt(0);
	private static final int space = " ".codePointAt(0);
	
	public static void main(String[] args) throws IOException {
//		 separate_file("data/trad/train_trad.data", "valid_trad", "utf-16");
//		 validate(Converter.read_file("valid_trad/result_crf.txt", "utf-8"), Converter.read_file("valid_trad/result_correct.txt", "utf-8"));
		
		// 简体
//		separate_file("data/simp/train_simp.data", "valid_simp", "utf-8");
		validate(Converter.read_file("valid_simp/result_crf.txt", "utf-8"), Converter.read_file("valid_simp/result_correct.txt", "utf-8"));

	}
	
	private static void separate_file(String input_file, String output_folder, String encoding) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(input_file), Charset.forName(encoding)));
		
		ArrayList<String> lines = new ArrayList<String>();
		String s;
		int total_size = 0;
		
		while ((s = in.readLine()) != null) {
			lines.add(s);
			total_size += s.length();
		}
		int threshold_size = (int) (total_size * 0.3d);

		Random random = new Random();
		StringBuilder sb_train = new StringBuilder();
		StringBuilder sb_test = new StringBuilder();
		
		int i = 0;
		for (; i < lines.size(); ++i) {
			
			if (sb_test.length() > threshold_size) break;
			
			else {
				String line = lines.get(i);
				
				if (random.nextDouble() < 0.3d)
					sb_test.append(line + "\n");
				else
					sb_train.append(line + "\n");
			}
		}
		
		for (; i < lines.size(); ++i) 
			sb_train.append(lines.get(i) + "\n");
		
		in.close();
		
		Converter.write_file(output_folder + "/train.txt", sb_train.toString(), "utf-8");
		Converter.write_file(output_folder + "/test.txt", sb_test.toString(), "utf-8");
	}
	
	private static double validate(String my_ans, String standard_ans) throws IOException { 
		count_words(my_ans, standard_ans);
		
		double precision = (double)num_correct_words / (double)num_my_words * 100d;
		double recall = (double)num_correct_words / (double)num_std_words * 100d;
		double f = 2 * precision * recall / (precision + recall);
		
		System.out.println("答案词数" + num_std_words);
		System.out.println("结果词数" + num_my_words);
		System.out.println("正确词数" + num_correct_words);
		
		DecimalFormat df = new DecimalFormat("###.00"); 
		System.out.println("准确率" + df.format(precision));
		System.out.println("召回率" + df.format(recall));
		System.out.println("F-值" + df.format(f));
		
		return f;
	}
	
	private static void count_words(String my_ans, String standard_ans) throws IOException {
		
		num_my_words = 0;
		num_std_words = 0;
		num_correct_words = 0;
			
		// ȥ����ͷ�ͽ�β���Ŀո�
		standard_ans = standard_ans.trim();
		my_ans = my_ans.trim(); 
		
		// ��ǰ�з�λ��
		int endofword_std = 0;
		int endofword_my = 0; 
		
		// ��ǰ�з�λ�õĺ�һ���ʵĿ�ʼλ��
		int beginofword_std = 0; 
		int beginofword_my = 0; 
		
		int[] length_and_next_ans;
		int[] length_and_next_result;
		
		while (beginofword_std < standard_ans.length() && beginofword_my < my_ans.length()) {
			
			length_and_next_ans = get_word_length_and_next_index(standard_ans, beginofword_std);
			length_and_next_result = get_word_length_and_next_index(my_ans, beginofword_my);
			
			if (endofword_std == endofword_my) {
				// �з�λ�ö�����
				endofword_std += length_and_next_ans[0];
				endofword_my += length_and_next_result[0];
				
				beginofword_std = length_and_next_ans[1];
				beginofword_my = length_and_next_result[1];
				
				if (endofword_std == endofword_my) {
					num_correct_words++;
				}
				
				num_std_words++;
				num_my_words++;
			} else if (endofword_std < endofword_my) {
				endofword_std += length_and_next_ans[0];
				beginofword_std = length_and_next_ans[1];
				
				num_std_words++;
			} else {
				endofword_my += length_and_next_result[0];
				beginofword_my = length_and_next_result[1];
				
				num_my_words++;
			}
		}
		
		while (beginofword_std < standard_ans.length()) {
			 beginofword_std = get_word_length_and_next_index(standard_ans, beginofword_std)[1];
			 num_std_words++;
		} 
		
		while (beginofword_my < my_ans.length()) {
			beginofword_my = get_word_length_and_next_index(my_ans, beginofword_my)[1];
			num_my_words++;
		}
	}
	
	private static int[] get_word_length_and_next_index(String text, int index) {
		int oldindex = index;
		int current;
		
		while (index < text.length()) {
			
			current = text.codePointAt(index);
			
			if (current == new_line || current == space) {
				break;
			}
			
			index = next_index(text, index);
		}
		
		int[] ret = new int[2];
		ret[0] = text.codePointCount(oldindex, index);
		
		while (index < text.length()) {
			current = text.codePointAt(index);
			
			if (current != new_line && current != space) {
				break;
			}
			
			index = next_index(text, index);
		}
		
		ret[1] = index;
		return ret;
	}
	
	private static int next_index(String text, int index) {
		return index + Character.charCount(text.codePointAt(index));
	}
}
