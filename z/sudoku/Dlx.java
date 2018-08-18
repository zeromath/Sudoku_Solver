package z.sudoku;

public class Dlx{
	static final int BASEN = 3;
	static final int DBASEN = BASEN*BASEN;
	static final int COL = 4*DBASEN*DBASEN;
	static final int ROW = DBASEN*DBASEN*DBASEN;
	static final int SUM = 8*DBASEN*DBASEN*DBASEN;
	static final int MAXN = 100000;


	private int m_U[] = new int[SUM+1];
	private int m_D[] = new int[SUM+1];
	private int m_L[] = new int[SUM+1];
	private int m_R[] = new int[SUM+1];
	private int m_colx[] = new int[SUM+1];
	private int m_rowx[] = new int[SUM+1];
	private int m_col[] = new int[COL+1]; 
	private int m_cols[] = new int[COL+1]; 
	private int m_row[] = new int[ROW+1]; 
	private int m_ans[][] = new int[10][10000];
	private int m_tempans[] = new int[10000];

	private int m_sum;
	private int m_length;
	private int m_head;
	private int m_r;
	private int m_c;
	private int m_symbol;
		
	private void remove(int k){
		m_R[m_L[k]] = m_R[k];
		m_L[m_R[k]] = m_L[k];	
		for (int i = m_D[k] ; i != k ; i = m_D[i])
			for (int j = m_L[i] ; j != i ; j = m_L[j]){
					m_D[m_U[j]] = m_D[j];
					m_U[m_D[j]] = m_U[j];
					m_cols[m_colx[j]]--;
			}
	};

	private void resume(int k){
		for (int i = m_U[k] ; i != k ; i = m_U[i])
			for (int j = m_R[i] ; j != i ; j = m_R[j]){
					m_cols[m_colx[j]]++;
					m_U[m_D[j]] = j;
					m_D[m_U[j]] = j;
			}
		m_L[m_R[k]] = k;
		m_R[m_L[k]] = k;
	};

	private boolean dfs(int k){
		if (m_R[m_head] == m_head){
			if (m_symbol == 0 || m_length < m_symbol){
				m_ans[m_length][0] = k;
				for (int i = 0; i < k; i++)
					m_ans[m_length][i+1] = m_tempans[i];
				m_length++;
				if (m_length == m_symbol) return true;
				return false;
			}
			return true;
		}
	
		int min = MAXN;
		int c = 0;
		for (int t = m_R[m_head] ; t != m_head ; t = m_R[t]){
			if (m_cols[t] < min){
				min = m_cols[t];
				c = t;
			}
		}
	
		remove(c);
		
		for (int i = m_D[c] ; i != c ; i = m_D[i]){
			m_tempans[k] = m_rowx[i];
			for (int j = m_R[i] ; j != i ; j = m_R[j])
				remove(m_colx[j]);
			if (dfs(k+1)) return true;
			for (int j = m_L[i] ; j != i ; j = m_L[j])
				resume(m_colx[j]);
		}
	
		resume(c);
		return false;
	};

	public Dlx(){
		m_head = 0;
		m_sum = 0;
		m_length = 0;
		m_symbol = 0;
		m_r = ROW;
		m_c =COL;
		
		m_L[m_head] = m_head;
		m_R[m_head] = m_head;
		m_U[m_head] = m_head;
		m_D[m_head] = m_head;
		
		for (int i = 1 ; i <= m_c ; i++){
			m_sum++;
			m_col[i] = m_sum;
			m_U[m_sum] = m_sum;
			m_D[m_sum] = m_sum;
	
			m_L[m_sum] = m_L[m_head];
			m_R[m_sum] = m_head;
			m_R[m_L[m_sum]] = m_sum;
			m_L[m_R[m_sum]] = m_sum;

			m_cols[i] = 0;
			m_colx[m_sum] = i;
			m_rowx[m_sum] = 0;
		}
		
		
		for (int i = 1 ; i <= m_r ; i++){
			m_sum++;
			m_row[i] = m_sum;
			m_L[m_sum] = m_sum;
			m_R[m_sum] = m_sum;
	
			m_U[m_sum] = m_U[m_head];
			m_D[m_sum] = m_head;
			m_U[m_D[m_sum]] = m_sum;
			m_D[m_U[m_sum]] = m_sum;
			
			m_colx[m_sum] = 0;
			m_rowx[m_sum] = i;
		}
	};

	public void AddPoint(int x,int y){
		m_sum++;
		m_R[m_sum]=m_row[x];
		m_L[m_sum]=m_L[m_row[x]];
		m_L[m_R[m_sum]]=m_sum;
		m_R[m_L[m_sum]]=m_sum;
		
		m_D[m_sum]=m_col[y];
		m_U[m_sum]=m_U[m_col[y]];
		m_D[m_U[m_sum]]=m_sum;
		m_U[m_D[m_sum]]=m_sum;
		
		m_cols[y]++;
		m_colx[m_sum]=y;
		m_rowx[m_sum]=x;
	};

	public void Solve(int s){
		for (int i = 1; i <= m_r; i++){
			m_R[m_L[m_row[i]]] = m_R[m_row[i]];
			m_L[m_R[m_row[i]]] = m_L[m_row[i]];
		}
		m_symbol = (s>10)?10:s;
		dfs(0);
		return;
	}

	public void Clear(){
		m_sum = 0;
		m_length = 0;
		m_symbol = 0;
		m_r = ROW;
		m_c = COL;
		
		m_L[m_head] = m_head;
		m_R[m_head] = m_head;
		m_U[m_head] = m_head;
		m_D[m_head] = m_head;
		
		for (int i = 1 ; i <= m_c ; i++){
			m_sum++;
			m_col[i] = m_sum;
			m_U[m_sum] = m_sum;
			m_D[m_sum] = m_sum;
	
			m_L[m_sum] = m_L[m_head];
			m_R[m_sum] = m_head;
			m_R[m_L[m_sum]] = m_sum;
			m_L[m_R[m_sum]] = m_sum;
			
			m_cols[i] = 0;
			m_colx[m_sum] = i;
			m_rowx[m_sum] = 0;
		}

		for (int i = 1 ; i <= m_r ; i++){
			m_sum++;
			m_row[i] = m_sum;
			m_L[m_sum] = m_sum;
			m_R[m_sum] = m_sum;
	
			m_U[m_sum] = m_U[m_head];
			m_D[m_sum] = m_head;
			m_U[m_D[m_sum]] = m_sum;
			m_D[m_U[m_sum]] = m_sum;
			
			m_colx[m_sum] = 0;
			m_rowx[m_sum] = i;
		}
	};

	public int GetLen(){
		return m_length;
	};
	public int GetLen(int k){
		return m_ans[k][0];
	};
	public int GetElm(int k,int l){
		return m_ans[k][l];
	};
};