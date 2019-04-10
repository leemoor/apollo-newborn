package libsvm;
public class svm_problem implements java.io.Serializable
{
	// 用来存储样本序号、样本的目标变量Y、样本自变量X
	public int l;
	public double[] y;
	public svm_node[][] x;
}
