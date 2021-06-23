package at.ac.fhcampuswien.newsanalyzer.downloader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;

public class ParallelDownloader extends Downloader{
    @Override
    public int process(List<String> urlList){
        long startTime = System.nanoTime();
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            List<Future<String>> allFutures = new ArrayList<>();
            //add task to the pool and add futures to list
            for (int i = 0; i < urlList.size(); i++) {
                Future<String> future = pool.submit(new Task(urlList.get(i)));
                allFutures.add(future);
            }
            //wait for every future to finish, then continue
            System.out.println("Waiting for downloads to finish...");
            for (Future<String> future : allFutures) {
                String result = future.get();
            }

            pool.shutdown();

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;
            System.out.println("Downloaded all articles in : " + duration + "ms!");
        } catch (ExecutionException | InterruptedException e){
            System.out.println(e);
        }
        return urlList.size();
    }

    private class Task implements Callable<String>{
        private final String url;
        public Task(String url){
            this.url = url;
        }
        @Override
        public String call(){
            //System.out.println("I am a Thread and I am about to download.");
            try {
                saveUrl2File(url);
            } catch (Exception e){
                System.out.println("Error: "+ e);
            }
            return "Done";
        }
    }
}