package hust.tools.ca.pos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 将北大人民日报词性标注语料转换成OpenNLP格式
 * 
 * 将  w/t 转换成 w_t 格式
 * 
 * 将 [w1/t1 w2/t2]t3 转换成 w1_t1 w2_t2 格式
 * 
 * @author 刘小峰
 */
public class CorpusConverter
{
    private static void usage()
    {
        System.out.println(CorpusConverter.class.getName() + " <pdCorpusFile> <outFile> [encoding]");
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length < 1)
        {
            usage();
            return;
        }

        String source = args[0];
        String dest = args[1];
        String encoding = "GBK";

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(source), encoding));
        String sentence = null;
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(dest), encoding));
        while ((sentence = in.readLine()) != null)
        {
            String[] wordtags = sentence.split("\\s");

            for (int i = 0; i < wordtags.length; i++)
            {
                String wordtag = wordtags[i];

                int pos = wordtag.lastIndexOf("[");
                if (pos >= 0)
                    wordtag = wordtag.substring(pos + 1);

                pos = wordtag.lastIndexOf("]");
                if (pos >= 0)
                    wordtag = wordtag.substring(0, pos);

                pos = wordtag.lastIndexOf("/");
                if (pos < 0)
                    continue;

                String word = wordtag.substring(0, pos);
                String tag = wordtag.substring(pos + 1);

                out.append(word + "_" + tag);
                if (i != wordtags.length - 1)
                    out.append(" ");
            }

            out.append("\n");
        }

        out.close();
        in.close();
    }
}
