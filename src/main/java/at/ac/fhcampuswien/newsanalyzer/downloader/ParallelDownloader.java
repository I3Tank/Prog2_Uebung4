package at.ac.fhcampuswien.newsanalyzer.downloader;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;

public class ParallelDownloader extends Downloader{
    @Override
    public int process(List<String> urlList) {
        long startTime = System.nanoTime();
        ExecutorService pool = Executors.newFixedThreadPool(3);
        Future<String> future = pool.submit(() -> {
            for (int i = 0; i < urlList.size(); i++) {
                Worker worker = new Worker(urlList.get(i));
                pool.execute(worker);
            }
            return "Download complete.";
        });

        while(!future.isDone()){
            System.out.println("Waiting for download completion...");
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        try{
            String result = future.get();
            System.out.println("Result: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("Downloaded all Articles in : " + duration + "ms!");
        return urlList.size();
    }

    private class Worker implements Runnable{
        private final String url;

        public Worker(String url){
            this.url = url;
        }
        @Override
        public void run() {
            System.out.println("I am a Thread and i am about to download.");
            try {
                saveUrl2File(url);
            } catch (Exception e){
                System.out.println("Error: "+ e);
            }
        }
    }
}
/*
    @Override
    public int process(List<String> urlList) {
        long startTime = System.nanoTime();
        ExecutorService pool = Executors.newFixedThreadPool(6);

        for (int i = 0; i < urlList.size(); i++) {
            Worker worker = new Worker(urlList.get(i));
            pool.execute(worker);
        }

        pool.shutdown();

        while(!pool.isTerminated()){}
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        System.out.println("Downloaded all Articles in : " + duration + "ms!");
        return urlList.size();
    }
* */