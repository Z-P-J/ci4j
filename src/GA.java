class GA {

    // 染色体数量
    private static final int CHROMOSOME_NUM = 10;
    // 基因总数
    private static final int GENE_NUM = 46;
    // 一个种群中染色体数量
    private String[] ipop = new String[CHROMOSOME_NUM];
    private int generation = 0;

    // 函数最优解
    private double bestFitness = Double.MAX_VALUE;
    // 最优代数
    private int bestGeneration = 0;
    // 最优染色体
    private String bestChromosome;

    public GA() {
        ipop = initPop();
    }

    /**
     * 随机初始化一条染色体
     */
    private String initChromosome() {
        String res = "";
        for (int i = 0; i < GENE_NUM; i++) {
            if (Math.random() > 0.5) {
                res += "1";
            } else {
                res += "0";
            }
        }
        return res;
    }

    /**
     * 初始化一个种群（10条染色体）
     */
    private String[] initPop() {
        String[] res = new String[CHROMOSOME_NUM];
        for (int i = 0; i < CHROMOSOME_NUM; i++) {
            res[i] = initChromosome();
        }
        return res;
    }

    /**
     * 计算染色体的fitness（适应度）
     * @param gene 一个个体的基因字符串
     */
    private double[] caculateFitness(String gene) {
        
        int a = Integer.parseInt(gene.substring(0, 23), 2);
        int b = Integer.parseInt(gene.substring(23, 46), 2);
        System.out.println("a=" + a + "  b=" + b);

        double x = a * (6.0 - 0) / (Math.pow(2, 23) - 1);
        double y = b * (6.0 - 0) / (Math.pow(2, 23) - 1);
        System.out.println("x=" + x + "  y=" + y);

        double temp1 = Math.sin(2 * x);
        double temp2 = Math.sin(2 * y);
        double fitness = 3 - temp1 * temp1 - temp2 * temp2;
        System.out.println("fitness=" + fitness);
        double[] res = {x, y, fitness};
        return res;
    }

    /**
     * 轮盘赌
     * 计算种群中每个个体的适应度
     * 根据适应度选择下一代个体
     */
    private void select() { // String[] ipop
        // 各个个体的适应度
        double[] fitnessArr = new double[CHROMOSOME_NUM];
        // 各个个体选中的概率
        double[] selectionProbability = new double[CHROMOSOME_NUM];
        // 累计概率
        double[] cumulativeProbability = new double[CHROMOSOME_NUM];
        // 合计适应度
        int totalFitness = 0;

        for (int i = 0; i < CHROMOSOME_NUM; i++) {
            double fitness = caculateFitness(ipop[i])[2];
            fitnessArr[i] = fitness;
            totalFitness += fitness;
            if (fitness < bestFitness) {
                bestFitness = fitness;
                bestGeneration = generation;
                bestChromosome = ipop[i];
            }
        }

        for (int i = 0; i < CHROMOSOME_NUM; i++) {
            double probability = fitnessArr[i] / totalFitness;
            selectionProbability[i] = probability;
            if (i == 0) {
                cumulativeProbability[i] = probability;
            } else {
                cumulativeProbability[i] = cumulativeProbability[i - 1] + probability;
            }
        }

        // // 排序，将概率大的放在前面
        // for (int i = 0; i < selectionProbability.length - 1; i++) { 
        //     for (int j = 0; j < selectionProbability.length - 1 - i; j++) { 
        //         if (selectionProbability[j] < selectionProbability[j + 1]) { 
        //             double temp = selectionProbability[j + 1]; 
        //             selectionProbability[j + 1] = selectionProbability[j]; 
        //             selectionProbability[j] = temp; 
        //         }
        //     }
        // }

        // for (int i = 0; i < CHROMOSOME_NUM; i++) {
        //     if (i == 0) {
        //         cumulativeProbability[i] = selectionProbability[i];
        //     } else {
        //         cumulativeProbability[i] = cumulativeProbability[i - 1] + selectionProbability[i];
        //     }
        // }

        for (int i = 0; i < CHROMOSOME_NUM; i++) {
            double random = Math.random();
            if (random < cumulativeProbability[0]) {
                ipop[i] = ipop[0];
            } else {
                for (int j = 1; j < CHROMOSOME_NUM; j++) {
                    if (random < cumulativeProbability[j]) {
                        ipop[i] = ipop[j];
                    }
                } 
            }
        }
    }

    /**
     * 交叉操作 交叉率为60%
     */
    private void cross() {
        for (int i = 0; i < CHROMOSOME_NUM; i++) {
            int next = (i + 1) % CHROMOSOME_NUM;
            if (Math.random() < 0.6) {
                int position = (int) (Math.random() * GENE_NUM) + 1;
                String temp1 = ipop[i].substring(0, position) + ipop[next].substring(position);
                String temp2 = ipop[next].substring(0, position) + ipop[i].substring(position);
                ipop[i] = temp1;
                ipop[next] = temp2;
            }
        }
    }

    /**
     * 基因突变 1%的基因变异率
     */
    private void mutation() {
        for (int i = 0; i < CHROMOSOME_NUM; i++) {
            String originChromosome = ipop[i];
            for (int j = 0; j < originChromosome.length(); j++) {
                if (Math.random() < 0.01) {
                    String mutationValue = originChromosome.charAt(j) == '0' ? "1" : "0";
                    ipop[i] = originChromosome.substring(0, j) 
                        + mutationValue 
                        + originChromosome.substring(j + 1);
                }
            }

            // if (Math.random() < 0.01) {
            //     // 变异基因处于所有基因的位置
            //     int mutationPosition = (int) (Math.random() * GENE_NUM * CHROMOSOME_NUM) + 1;
            //     // 基因突变的染色体位置
            //     int chromosomePosition = (mutationPosition / GENE_NUM) + 1;
            //     // 变异基因在该染色体中的位置
            //     int mutationPositionInChromosome = mutationPosition - (chromosomePosition - 1) * GENE_NUM;
            //     if (mutationPositionInChromosome <= 0) {
            //         mutationPositionInChromosome = 1;
            //     }
            //     chromosomePosition -= 1;
            //     if (chromosomePosition >= CHROMOSOME_NUM) {
            //         chromosomePosition = CHROMOSOME_NUM - 1;
            //     }
            //     // 开始变异
            //     String mutationValue;
            //     if (ipop[chromosomePosition].charAt(mutationPositionInChromosome) == '0') {
            //         mutationValue = "1";
            //     } else {
            //         mutationValue = "0";
            //     }
            //     String originChromosome = ipop[chromosomePosition];
            //     if (mutationPositionInChromosome == 1) {
            //         ipop[chromosomePosition] = mutationValue + originChromosome.substring(1);
            //     } else if (mutationPositionInChromosome == GENE_NUM) {
            //         ipop[chromosomePosition] = originChromosome.substring(0, mutationPositionInChromosome - 1) + mutationValue;
            //     } else {
            //         ipop[chromosomePosition] = originChromosome.substring(0, mutationPositionInChromosome - 1) 
            //             + mutationValue 
            //             + originChromosome.substring(mutationPositionInChromosome);
            //     }
            // }
        }
    }

    public static void main(String[] args) {
        GA ga = new GA();

        for (int i = 0; i < 100000; i++) {
            ga.select();
            ga.cross();
            ga.mutation();
            ga.generation = i;
        }
        double[] best = ga.caculateFitness(ga.bestChromosome);
        System.out.println("最小值" + ga.bestFitness 
            + "\n第" + ga.bestGeneration + "代染色体：<" + ga.bestChromosome + ">" 
            + "\nx=" + best[0] + "\ty=" + best[1]);
    }



}