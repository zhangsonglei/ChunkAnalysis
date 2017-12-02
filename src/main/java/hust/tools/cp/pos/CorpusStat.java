package hust.tools.cp.pos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 词性标注语料统计程序
 * 
 * 词和词性间用_分隔
 * 
 * @author 刘小峰
 * 
 */
public class CorpusStat
{
    private static void usage()
    {
        System.out.println(CorpusStat.class.getName() + " -train <trainFile> [-test testFile] [-details] [encoding]");
    }

    /**
     * 构建词性标注语料的词典
     * 
     * 适用于OpenNLP格式
     * 
     * @param corpusFile
     *            语料文件
     * @param encoding
     *            语料文件编码
     * @return 词典
     * @throws IOException
     */
    public static HashSet<String> buildDict(String corpusFile, String encoding) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(corpusFile), encoding));

        HashSet<String> dict = new HashSet<String>();

        String sentence;
        while ((sentence = in.readLine()) != null)
        {

            String[] wordtags = sentence.split("\\s");

            for (int i = 0; i < wordtags.length; i++)
            {
                String wordtag = wordtags[i];
                int pos = wordtag.lastIndexOf("_");

                if (pos < 0)
                    continue;

                String word = wordtag.substring(0, pos);

                dict.add(word);
            }
        }

        in.close();

        return dict;
    }

    /**
     * 统计词性标注语料的各种数据
     * 
     * @param source
     *            语料文件
     * @param encoding
     *            语料文件编码
     * @param dict
     *            词典，当为null时不统计oov
     * @throws IOException
     */
    public static void stat(String source, String encoding, boolean details, HashSet<String> dict) throws IOException
    {
        System.out.println("语料统计数据 for " + source);
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(source), encoding));
        String sentence = null;
        int nSentences = 0;
        int nWordTokens = 0;
        int nChars = 0;
        HashMap<String, HashSet<String>> word2Tags = new HashMap<String, HashSet<String>>();
        HashMap<String, Integer> tag2Count = new HashMap<String, Integer>();

        int oov = 0;
        while ((sentence = in.readLine()) != null)
        {
            if (sentence.length() == 0)
                continue;
            nSentences++;

            String[] wordtags = sentence.split("\\s");

            for (int i = 0; i < wordtags.length; i++)
            {
                String wordtag = wordtags[i];

                int pos = wordtag.lastIndexOf("_");

                if (pos < 0)
                    continue;

                String word = wordtag.substring(0, pos);
                String tag = wordtag.substring(pos + 1);

                if (dict != null && !dict.contains(word))
                    oov++;

                nWordTokens++;

                nChars += word.length();

                if (!word2Tags.containsKey(word))
                {
                    HashSet<String> tags = new HashSet<String>();
                    tags.add(tag);

                    word2Tags.put(word, tags);
                }
                else
                {
                    HashSet<String> tags = word2Tags.get(word);
                    tags.add(tag);

                    word2Tags.put(word, tags);
                }

                if (!tag2Count.containsKey(tag))
                {
                    tag2Count.put(tag, 1);
                }
                else
                {
                    int n = tag2Count.get(tag);
                    n++;

                    tag2Count.put(tag, n);
                }
            }

        }

        in.close();

        System.out.println("句子数: " + nSentences);
        System.out.println("词条数: " + nWordTokens);
        System.out.println("词形数: " + word2Tags.size());
        System.out.println("字数: " + nChars);
        System.out.println("词性数: " + tag2Count.size());

        if (dict != null)
            System.out.println("OOV率: " + (double)oov / nWordTokens);

        if (details)
        {
            for (Map.Entry<String, Integer> e : tag2Count.entrySet())
                System.out.println(e.getKey() + "\t" + e.getValue());

            for (Map.Entry<String, HashSet<String>> e : word2Tags.entrySet())
            {
                if (e.getValue().size() > 1)
                {
                    System.out.print(e.getKey() + ": ");

                    for (String t : e.getValue())
                        System.out.print(t + "\t");

                    System.out.println();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length < 1)
        {
            usage();
            return;
        }

        String trainFile = null;
        String testFile = null;
        String encoding = "GBK";
        boolean details = false;
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-train"))
            {
                trainFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-test"))
            {
                testFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            }
            else if (args[i].equals("-details"))
            {
                details = true;
                i++;
            }
        }

        if (testFile == null)
            stat(trainFile, encoding, details, null);
        else
        {
            stat(trainFile, encoding, details, null);

            HashSet<String> dict = buildDict(trainFile, encoding);
            stat(testFile, encoding, details, dict);
        }
    }
}
