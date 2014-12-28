package com.odk.pairpongsender;

public class ScoreList {
	public Score[] scores= new Score[5];
	ScoreList( int[] scores, String[] names, String[] dates, String[] options){
		for(int n=0;n<5;n++)
			this.scores[n]= new Score(scores[n],names[n],dates[n],options[n]);
	}
	public int getRank(int score){
		if (score<scores[4].score)
			return 6;
		else if (score<scores[3].score)
			return 5;
		else if (score<scores[2].score)
			return 4;
		else if (score<scores[1].score)
			return 3;
		else if (score<scores[0].score)
			return 2;
		else
			return 1;
	}
	public void update(int rank, int score, String name, String date, String option){
		for(int n=4;n>rank-1;n--){
			scores[n]=scores[n-1];
		}
		scores[rank-1]=new Score(score, name, date, option);
	}
	class Score{
		public int score;
		public String name;
		public String date;
		public String option;
		Score(int s, String n, String d, String o){
			score=s;
			name=n;
			date=d;
			option=o;
		}
	}
}

