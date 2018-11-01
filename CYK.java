import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

public class CYK {
    public static void main(String[] args) {
        /* Step1:
         Read the file of sentence
         we need to change every sentence file path here
        */
        //File sentsFile =new File("/Users/shaolongchen/Desktop/pa3-cky/sents.txt");
        String sentsFile = "/Users/shaolongchen/Desktop/pa3-cky/sents.txt";
        ArrayList<String> sentence = new ArrayList<>();
        // all the words are in the List of String named "sentence"
        sentence = readfile(sentsFile);
        //for(String cur : sentence)
        //    System.out.println(cur);
        String processSen = "";
        for(String cur :  sentence) {
            processSen += (" ");
            processSen += cur;
        }
        System.out.println("PROCESSING SENTENCE:" + processSen);
        //######################################################################################
        /*
            Step2 : read the file of Grammar
            Name of method is readGrammar, input is the path of the grammar file,
            and return type is List of String.
            Every line in the grammar file is in a string.
            Notice: we need to change the path of grammar file here
         */
        ArrayList<String> grammar = new ArrayList<>();
        String grammarFile = "/Users/shaolongchen/Desktop/pa3-cky/grammar_rules.txt";
        grammar = readGrammar(grammarFile);
        //for(String cur : grammar)
        //    System.out.println(cur);
        HashMap<String, ArrayList<String>> mapOfGrammar = transGrammarToHashMap(grammar);
        // key : one string
        // value: list of string, the last one is double value, remember to transfer to double number
        // other string in the value are string.
        //######################################################################################
        /* Step 3: CYK algorithm
            1. one words processing
            2. Multiple words processing
            3. print output
        */
        //*******************************************************************
        /*
        Step3.1 one word processing
        Data structure : ArrayList, 3D, cell == list of string , every outcome
        every line contains the this line of cell
        all the table are in the 3D list.
        private static ArrayList<String>[][] table, length = sentence,length
         */
        ArrayList<String>[][] table = new ArrayList[sentence.size()][sentence.size()];
        for(int tablei = 0; tablei < table.length; tablei ++) {
            table[tablei] = new ArrayList[sentence.size()];
            for(int tablej = tablei; tablej < table.length; tablej ++) {
                ArrayList<String> newcell = new ArrayList<String>();
                table[tablei][tablej] = newcell;
            }
        }
         HashSet<String> setOfWords = new HashSet<>();
         setOfWords = parseWordIntoSet(sentence);
         /*for(String key : mapOfGrammar.keySet()){
                List<String> curList = mapOfGrammar.get(key);
                if(setOfWords.contains(curList.get(0)){

                }
         }
         */
         // initialize the 2D array, every cell is a List of String
            for(int i = 0; i < sentence.size(); i ++) {
                // 找每个单词
                String curWord = sentence.get(i);
                for(String key : mapOfGrammar.keySet() ){
                    List<String> curList = new ArrayList<>(mapOfGrammar.get(key));
                    for(int j = 0; j < curList.size(); j ++) {
                        String curStr = curList.get(j);
                       // System.out.println(curWord);
                        if(curWord.equals(curStr.split("\\s+")[1])) {
                        // 按照空格划分啊，千万不能直接打空格，会错的，要用"\\s+"的形式
                        // 盖了一晚上啊，这个bug，哭了，还有10个小时
                            String newPath = key + " " + curStr.split("\\s+")[1] + " " + curStr.split("\\s+")[2];
                            table[i][i].add(newPath);
                        }
                    }
                }
            }
            // step 3.2 find the final path to the S and calculate the number of possibility
            // let me think

           // System.out.println(table[0][0]);

            for(int i = 0; i < table.length; i ++) {
                HashSet<String> setOfSingleWord = new HashSet<>();
                ArrayList<String> strA = new ArrayList<>();
                for(int j = 0; j < table[i][i].size(); j ++) {
                    // record all the existing strings
                    setOfSingleWord.add(table[i][i].get(j).split("\\s+")[0]);
                    strA.add(table[i][i].get(j).split("\\s+")[0]);
                }
                while(!setOfSingleWord.contains("S")) {
                    for (String curKey : strA) {
                        for (String key : mapOfGrammar.keySet()) {
                            List<String> curList = new ArrayList<>(mapOfGrammar.get(key));
                            for (int j = 0; j < curList.size(); j++) {
                                String curStr = curList.get(j);
                                // System.out.println(curWord);
                                if (curKey.equals(curStr.split("\\s+")[1]) && curStr.split("\\s+").length == 3) {
                                    // 按照空格划分啊，千万不能直接打空格，会错的，要用"\\s+"的形式
                                    // 盖了一晚上啊，这个bug
                                    List<String> curCell = table[i][i];
                                    double time = 0;
                                    for (int c = 0; c < curCell.size(); c++) {
                                        if (curCell.get(c).split("\\s+")[0].equals(curKey)) {
                                            time = Double.parseDouble(curCell.get(c).split("\\s+")[2]);
                                            break;
                                        }
                                    }
                                    String newPath = key + " " + curStr.split("\\s+")[1] + " " +
                                            (Double.parseDouble(curStr.split("\\s+")[curStr.split("\\s+").length - 1]) * time);
                                    table[i][i].add(newPath);
                                }
                            }
                            setOfSingleWord.add(key);
                        }

                    }
                }
            }
            // 原来的结果少一步到S的结果，需要多写一个循环
          for(int i = 0; i < table.length; i ++) {
              List<String> findfind = table[i][i];
              for(int j = 0; j < findfind.size(); j ++) {
                  String[] curStr2 = findfind.get(j).split("\\s+");
                  String curHead = curStr2[0];
                  for (String key : mapOfGrammar.keySet()) {
                      if(!key.equals("S"))
                          continue;
                      List<String> curList = new ArrayList<>(mapOfGrammar.get(key));
                      for (int j1 = 0; j1 < curList.size(); j1++) {
                          String curStr = curList.get(j1);
                          // System.out.println(curWord);
                          if (curHead.equals(curStr.split("\\s+")[1]) && curStr.split("\\s+").length == 3) {

                              double time = Double.parseDouble(curStr2[2]);
                              /*for (int c = 0; c < curCell.size(); c++) {
                                  if (curCell.get(c).split("\\s+")[0].equals(curKey)) {
                                      time = Double.parseDouble(curCell.get(c).split("\\s+")[2]);
                                      break;
                                  }
                              }*/
                              String newPath = key + " " + curStr.split("\\s+")[1] + " " +
                                      (Double.parseDouble(curStr.split("\\s+")[curStr.split("\\s+").length - 1]) * time);
                              table[i][i].add(newPath);
                          }
                      }
                       // add result
                  }
              }
           }

        //System.out.println(table[3][3]);

         /* 1. use the HashSet to check whether the key words are in the grammar
            2. every layer repeat the same method until we find the S
            3. Don't forget to time the possibilities in double
          */
        //*******************************************************************
        /*
        Step3.2: Multiple words processing
            (1) DP algorithm, check the same string
            (2)
         */
        for(int len = 2; len <= sentence.size(); len ++) {
            for(int i = 0; i < sentence.size() - len + 1; i ++) {
                int j = i + len - 1;
                // 在table中的坐标是（i，j）=》table[i][j]
                // go through all the combine of 2 cells
                HashMap<String, Double> mapOfThisCell = new HashMap<>();
                for(int colOfSearch = i; colOfSearch < j; colOfSearch ++) {
                    int rowOfSearch =  1  + colOfSearch;
                    // 从两个cell进行查找，一个是（i, colOfSearch）and (rowOfSearch,j)
                    // 将两个cell中的元素放在两个新建的list中
                    List<String> list1 = table[i][colOfSearch];
                   // List<String> list2 = table[rowOfSearch][j];
                    List<String> list2 = table[rowOfSearch][j];
                    for(int l1 = 0; l1< list1.size(); l1++) {
                        for (int l2 = 0; l2 < list2.size(); l2++) {
                            // go through the grammar hashmap to check this whether this combo exist or not
                            // do not find S in this loop

                            String f1 = list1.get(l1).split("\\s+")[0];
                            String f2 = list2.get(l2).split("\\s+")[0];
                            for (String key : mapOfGrammar.keySet()) {
                                if(key.equals("S"))
                                    continue;
                                List<String> curGrammar = mapOfGrammar.get(key);

                                for(int fu = 0; fu < curGrammar.size(); fu ++) {
                                    String curTrouble = curGrammar.get(fu);
                                    String[] touble = curTrouble.split("\\s+");
                                    if (touble[1].equals(f1) && touble[2].equals((f2))) {
                                        String curRes = key + " " + f1 + " " + f2;
                                        double resNum = Double.parseDouble(curGrammar.get(fu).split("\\s+")[curGrammar.get(fu).split("\\s+").length - 1]) *
                                                Double.parseDouble(list1.get(l1).split("\\s+")[list1.get(l1).split("\\s+").length - 1]) *
                                                Double.parseDouble(list2.get(l2).split("\\s+")[list2.get(l2).split("\\s+").length -1]);
                                        if (!mapOfThisCell.containsKey(curRes)) {
                                            table[i][j].add(curRes + " " + Double.toString(resNum));
                                            mapOfThisCell.put(curRes, resNum);
                                        } else {
                                            // we need to campare two result
                                            // add the before pointer
                                            if (resNum > mapOfThisCell.get(curRes)) {
                                                table[i][j].add(curRes + " " + Double.toString(resNum));
                                                mapOfThisCell.put(curRes, resNum);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // System.out.println(table[0][1]);
       // System.out.println(table[3][3]);
        // find the path to S, just find the largest possibility path
        // refill S in every cell

        for(int len = 2; len <= sentence.size(); len ++) {
            for(int i = 0; i < sentence.size() - len + 1; i ++) {
                int j = i + len - 1;
                // 在table中的坐标是（i，j）=》table[i][j], finish S
                // go through all the combine of 2 cells
                // use hashmap to find the path to S and  find the largest possilbilty
                HashMap<String, Double> mapOfThisCell = new HashMap<>();
                List<String> curGrammar = mapOfGrammar.get("S");
                List<String> curCell = table[i][j];


                double vp = 0;
                double np = 0;
                for(int i2 = 0; i2 <= 1; i2 ++) {
                    if(curCell.get(i2).split("\\s+")[0].equals("NP")) {
                        np = Double.parseDouble(curCell.get(i2).split("\\s+")[curCell.get(i2).split("\\s+").length - 1]);
                    } else {
                        vp = Double.parseDouble(curCell.get(i2).split("\\s+")[curCell.get(i2).split("\\s+").length - 1]);
                    }
                }

                double maxOfRes2 = vp * np;
                String sss = "";
                for(int fo = 0; fo < curGrammar.size(); fo++) {
                    String curCe = curGrammar.get(fo);
                    if(curCe.split("\\s+")[1].equals("VP") &&
                            Double.parseDouble(curCe.split("\\s+")[curCe.split("\\s+").length - 1]) * vp
                              > maxOfRes2) {
                        maxOfRes2 =  Double.parseDouble(curCe.split("\\s+")[curCe.split("\\s+").length - 1]) * vp;
                        sss = "S" + " " + "VP" + " " + Double.toString(maxOfRes2);
                    }
                }
                // the final S of each cell is "sss"
                //System.out.println(sss);
                // find the poss of NP+VP and  VP
                double snv = 0;
                double sv = 0;
                for(int ik = 0; ik < curGrammar.size(); ik ++) {
                    if(curGrammar.get(ik).split("\\s+")[1].equals("NP"))
                        snv = Double.valueOf(curGrammar.get(ik).split("\\s+")[curGrammar.get(ik).split("\\s+").length - 1]);
                    else {
                        sv = Double.valueOf(curGrammar.get(ik).split("\\s+")[curGrammar.get(ik).split("\\s+").length - 1]);
                    }
                }



                for(int colOfSearch = i; colOfSearch < j; colOfSearch ++) {
                    int rowOfSearch =  1  + colOfSearch;
                    // 从两个cell进行查找，一个是（i, colOfSearch）and (rowOfSearch,j)
                    // 将两个cell中的元素放在两个新建的list中
                    // find all the NP-Vp pair

                    List<String> list1 = table[i][colOfSearch];
                    // List<String> list2 = table[rowOfSearch][j];
                    List<String> list2 = table[rowOfSearch][j];
                    for(int l1 = 0; l1< list1.size(); l1++) {
                        for (int l2 = 0; l2 < list2.size(); l2++) {
                            // go through the grammar hashmap to check this whether this combo exist or not
                            // do not find S in this loop
                            String f1 = list1.get(l1).split("\\s+")[0];
                            String f2 = list2.get(l2).split("\\s+")[0];
                            if(f1.equals("NP") && f2.equals("VP")) {
                                double tempNp = 0;
                                double tempVp = 0;
                                for(int i23 = 0; i23 < 2;  i23 ++) {
                                    if(list1.get(i23).split("\\s+")[0].equals("NP")){
                                        tempNp = Double.valueOf(list1.get(i23).split("\\s+")[list1.get(i23).split("\\s+").length - 1]);
                                    }
                                }
                                for(int i23 = 0; i23 < 2;  i23 ++) {
                                    if(list2.get(i23).split("\\s+")[0].equals("VP")){
                                        tempVp = Double.valueOf(list1.get(i23).split("\\s+")[list1.get(i23).split("\\s+").length - 1]);
                                    }
                                }

                                if(tempNp * tempVp *  snv > maxOfRes2) {
                                    maxOfRes2 = tempNp * tempVp;
                                    sss = "S NP VP " + Double.toString(maxOfRes2);
                                }
                             } else if(f1.equals("VP")) {

                                double tempVp = 0;
                                for(int i23 = 0; i23 < 2;  i23 ++) {
                                    if(list1.get(i23).split("\\s+")[0].equals("VP")){
                                        tempVp = Double.valueOf(list1.get(i23).split("\\s+")[list1.get(i23).split("\\s+").length - 1]) * sv;
                                    }
                                }
                                    if(tempVp > maxOfRes2) {
                                        maxOfRes2 = tempVp;
                                        sss= "S VP " + Double.toString(maxOfRes2);
                                    }
                             } else if(f2.equals("VP")) {

                                double tempVp = 0;
                                for(int i23 = 0; i23 < 2;  i23 ++) {
                                    if(list2.get(i23).split("\\s+")[0].equals("VP")){
                                        tempVp = Double.valueOf(list1.get(i23).split("\\s+")[list1.get(i23).split("\\s+").length - 1]) *sv;
                                    }
                                }

                                if(tempVp > maxOfRes2) {
                                    maxOfRes2 = tempVp;
                                    sss= "S VP " + Double.toString(maxOfRes2);
                                }
                             } else {
                                continue;
                            }

                        }
                    }
                }
                //System.out.println(sss);
                table[i][j].add(sss);
            }
        }

        // System.out.println(table[0][1]);
        //*******************************************************************
        /*
        Step3.3 print output
        (1) print every cell in the 3D array
            every cell contains the path to the S and the number
            eg:
                SPAN: fish
                P(N) = 0.2
                P(V) = 0.6
                P(NP) = 0.14 (BackPointer = N)
                P(VP) = 0.06 (BackPointer = V)
                P(S) = 0.006 (BackPointer = VP)
         */
        // printing format 根据字符串长度做循环，从1到N
        // print function finished
       for(int len = 1; len <= sentence.size(); len ++) {
           for(int i = 0; i + len <= table.length; i ++){
               int j = i + len - 1;
               String span = new String("");
               // all the words of this loop stored in a string
               for(int k = i; k <= j; k ++) {
                   span += sentence.get(k);
                   span += " ";
               }
               System.out.println("SPAN: " + span);


               ArrayList<String> curList = table[i][j];
               if(len == 1) {
                   for(int indexOfString = 0; indexOfString < curList.size(); indexOfString ++) {
                        String[] curString2 = curList.get(indexOfString).split("\\s+");
                    if(setOfWords.contains(curString2[1])) {
                      String curRes = "P(";
                      curRes += (curString2[0] + " " + curString2[1] + ")=" + curString2[2]);
                      System.out.println(curRes);
                   } else {
                      System.out.println("P(" + curString2[0] + ")" + " = " + curString2[2] + " (BackPointer = " + curString2[1] + ")");
                  }
                }
               } else {
                      //****************
                      // len > 1, output format is different
                   for(int indexOfString = 0; indexOfString < curList.size(); indexOfString ++) {
                       String[] curString2 = curList.get(indexOfString).split("\\s+");
                            String b = "";
                          for(int finals = 1; finals < curString2.length - 1; finals ++) {
                              if(finals != curString2.length - 1)
                                  b += (curString2[finals]+ " ");
                              else
                                  b += (curString2[finals]+ " ");
                          }
                           System.out.println("P(" + curString2[0] + ")" + " = " + curString2[curString2.length - 1] + " (BackPointer = ( " + b + "))");
                       }


               }
               System.out.println("\n");
           }
       }
    }

        public static ArrayList<String> readfile (String path) {
            ArrayList<String> res = new ArrayList<>();
            try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

                /* 读入TXT文件 */
                 // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
                File filename = new File(path); // 要读取以上路径的input。txt文件
                InputStreamReader reader = new InputStreamReader(
                        new FileInputStream(filename)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = "";
                while ((line = br.readLine()) != null) {

                     String cur = line;
                     res.add(cur); // 一次读入一行数据
                }
                br.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            //把每行数据转换成单个字符串
            ArrayList<String> res2 = new ArrayList<>();
            for(String curInRes1 :  res) {
                String[] stringArray = curInRes1.split(" ");
                for(String singleStr : stringArray){
                    res2.add(singleStr);
                }
            }
            return res2;
        }
        public static ArrayList<String> readGrammar(String path){
            ArrayList<String> res = new ArrayList<>();
            try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

                /* 读入TXT文件 */
                // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
                File filename = new File(path); // 要读取以上路径的input。txt文件
                InputStreamReader reader = new InputStreamReader(
                        new FileInputStream(filename)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = "";
                while ((line = br.readLine()) != null) {

                    String cur = line;
                    res.add(cur); // 一次读入一行数据
                }
                br.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }
        public static HashMap<String, ArrayList<String>> transGrammarToHashMap(ArrayList<String> grammar) {
            HashMap<String, ArrayList<String>> map = new HashMap<>();
            for(int i = 0; i < grammar.size(); i ++) {
                String curString = grammar.get(i);
                String[] stringArray = curString.split(" ");
                String keyString = stringArray[0];
                if(!map.containsKey(keyString)) {
                    ArrayList<String> listValue = new ArrayList<>();
                    String curValue = new String();
                    for(int j = 1; j < stringArray.length; j ++) {
                            curValue += stringArray[j];
                            curValue += " ";
                    }
                    listValue.add(curValue);
                    map.put(keyString, listValue);
                }else {
                    String curValue = new String();
                    for(int j = 1; j < stringArray.length; j ++) {
                            curValue += stringArray[j];
                            curValue += " ";
                    }
                    map.get(keyString).add(curValue);
                }
            }
            return map;
        }
        public static HashSet<String> parseWordIntoSet(List<String> sentence){
            HashSet<String> setOfWords = new HashSet<>();
            for(String cur : sentence){
                setOfWords.add(cur);
            }
            return setOfWords;
        }
}
