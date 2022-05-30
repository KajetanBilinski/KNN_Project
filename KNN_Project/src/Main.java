import java.io.*;
import java.util.*;

public class Main {
    public static int k=10;
    public static double trainCount=0;
    public static double correctCount=0;
    public static double testCount=0;
    static HashMap<String,Integer> types=new HashMap<>();
    public static void main(String[] args)

    {
        File testFile =new File("src/iris.test.data");
        File trainFile =new File("src/iris.data");
        ArrayList<Data> trainData = new ArrayList<>();
        ArrayList<Data> testData = new ArrayList<>();
        try
        {
            Scanner scanner1 = new Scanner(testFile);
            Scanner scanner2 = new Scanner(trainFile);

            while(scanner1.hasNextLine())
            {
                String[] tmp = scanner1.nextLine().split(",");
                testData.add(splitator(tmp));
                testCount++;
            }
            while(scanner2.hasNextLine())
            {
                String[] tmp = scanner2.nextLine().split(",");
                trainData.add(splitator(tmp));
                types.put(tmp[tmp.length-1],0);
                trainCount++;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        doMath(testData,trainData);
        show(testData);
        System.out.println();
        System.out.println("Do you want to enter new vector for test?");
        System.out.println("[1] Yes");
        System.out.println("[2] No");
        boolean always=true;
        while(always)
        {
            Scanner scan = new Scanner(System.in);
            int option = scan.nextInt();
            if(option==2)
                System.exit(0);
            else if(option==1)
            {
                trainData.clear();
                System.out.println("Please enter the vectors in the following form: "+testData.get(0).vec.size()+" vector coordinates separated with \",\" and name of type");
                System.out.println("Next types separete with \" \" [space]");
                Scanner scan2= new Scanner(System.in);
                String all = scan2.nextLine();
                String [] linie = all.split(" ");
                testCount=0;
                for(String line : linie)
                {
                    String [] tmp =line.split(",");
                    trainData.add(splitator(tmp));
                    testCount++;
                }
                doMath(testData,trainData);
                show(testData);
                System.exit(0);
            }
            else
                System.exit(-1);
        }

    }

    private static Data splitator(String[] tmp)
    {
        ArrayList<Double> tmpArray = new ArrayList<>();
        for (int i = 0; i < tmp.length-1; i++)
            tmpArray.add(Double.parseDouble(tmp[i]));
        String name=tmp[tmp.length-1];
        return new Data(name,tmpArray);
    }

    private static void show(ArrayList<Data> testData)
    {
        for (int i = 1; i <k+1 ; i++)
        {
            double wynik =returnAccuracy(testData,i);
            StringBuilder sb = new StringBuilder();
            int goodCount=0;
            int badCount=0;
            for (int j = 1; j <testCount+1 ; j++)
            {
                if(j<=wynik)
                {
                    sb.append("◼");
                    goodCount++;
                }
                else
                {
                    sb.append("◻");
                    badCount++;
                }
            }
            sb.append("\t   ");
            sb.append(goodCount).append(" / ").append(badCount+goodCount);
            sb.append("\t   ");
            double percent =((goodCount*100)/testCount);
            sb.append(percent);
            System.out.println("k = "+i+"\t "+sb+ "%");
            correctCount=0;
        }
    }

    private static double returnAccuracy(ArrayList<Data> testData,int number)
    {
        for (Data testDatum : testData)
        {
            if(testDatum.checkGood(number))
                correctCount++;
        }
        return correctCount;
    }


    private static void doMath(ArrayList<Data> test,ArrayList<Data> train)
    {

        for (Data b : test)
        {
            for (Data a : train)
            {
                double result=0;
                for (int i = 0; i <a.vec.size() ; i++)
                    result+=Math.pow(a.vec.get(i)-b.vec.get(i),2);
                b.addResult(Math.sqrt(result),a.name);
            }
        }
    }

    public static class Data
    {
        String name;
        ArrayList<Results> results = new ArrayList<>();
        ArrayList<Double> vec;

        public Data(String name, ArrayList<Double> vec)
        {
            this.name = name;
            this.vec=vec;
        }

        public void addResult(double ostateczna,String nazwa)
        {
            results.add(new Results(nazwa,ostateczna));
        }

        public boolean checkGood(int z)
        {
            Collections.sort(results);
            for (int i = 0; i < z ; i++)
            {
                types.put(results.get(i).name, types.get(results.get(i).name)+1);
            }
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String,Integer> entry : types.entrySet())
            {
                sb.append(entry.getKey());
                sb.append(" ");
                sb.append(entry.getValue());
                sb.append(" || ");
            }
            String name=types.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
//          ======FOR DEBUG======
//            if(!nazwa.equals(this.name))
//                System.out.println("ERROR IN "+sb.toString()+" CORRECT WAS ="+this.name);
            types.entrySet().forEach(e->e.setValue(0));
            return name.equals(this.name);
        }
    }
    public static class Results implements Comparable<Results>
    {
        String name;
        double result;

        public Results(String name, double result)
        {
            this.name = name;
            this.result = result;
        }

        @Override
        public int compareTo(Results o)
        {
            return Double.compare(result,o.result);
        }
    }

}
