package com.heaton.advertisersdk.interceptor;

public class Strategy {
    private int productIndex;//产品码解析位置
    private int rollingIndex;//x1 x2 x3解析位置
    private int randomIndex;
    private int commandIndex;//命令码解析位置  如:C1

    Strategy(Builder builder){
        this.productIndex = builder.productIndex;
        this.rollingIndex = builder.rollingIndex;
        this.randomIndex = builder.randomIndex;
        this.commandIndex = builder.commandIndex;
    }

    public int getProductIndex() {
        return productIndex;
    }

    public void setProductIndex(int productIndex) {
        this.productIndex = productIndex;
    }

    public int getRollingIndex() {
        return rollingIndex;
    }

    public void setRollingIndex(int rollingIndex) {
        this.rollingIndex = rollingIndex;
    }

    public int getRandomIndex() {
        return randomIndex;
    }

    public void setRandomIndex(int randomIndex) {
        this.randomIndex = randomIndex;
    }

    public int getCommandIndex() {
        return commandIndex;
    }

    public void setCommandIndex(int commandIndex) {
        this.commandIndex = commandIndex;
    }

    public static class Builder {
        private int productIndex = 1;//产品码解析位置
        private int rollingIndex = 6;//x1 x2 x3解析位置
        private int randomIndex = 13;
        private int commandIndex = 4;//命令码解析位置  如:C1

        public Builder(){}

        Builder(Strategy strategy){
            this.productIndex = strategy.productIndex;
            this.rollingIndex = strategy.rollingIndex;
            this.randomIndex = strategy.randomIndex;
            this.commandIndex = strategy.commandIndex;
        }

        public Builder productIndex(int productIndex) {
            this.productIndex = productIndex;
            return this;
        }

        public Builder rollingIndex(int rollingIndex) {
            this.rollingIndex = rollingIndex;
            return this;
        }

        public Builder randomIndex(int randomIndex) {
            this.randomIndex = randomIndex;
            return this;
        }

        public Builder commandIndex(int commandIndex) {
            this.commandIndex = commandIndex;
            return this;
        }

        public Strategy build() {
            return new Strategy(this);
        }
    }

}
