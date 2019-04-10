package libsvm;

import com.apollo.crack.train.CaptchackerNew;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

public class svm_predict_ex {
	private static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}

	public static String predict(List<String> characteristicStrs, svm_model model) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		for (String line : characteristicStrs)
		{
			StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

			st.nextToken();
			int m = st.countTokens()/2;
			svm_node[] x = new svm_node[m];
			for(int j=0;j<m;j++)
			{
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}

			sb.append(CaptchackerNew.array[(int)svm.svm_predict(model,x)]);
//			sb.append((long)svm.svm_predict(model,x));
		}
		return sb.toString();

	}
}
