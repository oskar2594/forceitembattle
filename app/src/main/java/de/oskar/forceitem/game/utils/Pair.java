package de.oskar.forceitem.game.utils;

public class Pair<L, R> {
    private L left;
    private R right;

    public Pair(L left, R right) {
        this.setLeft(left);
        this.setRight(right);
    }

    public Pair() {
        this.setLeft(null);
        this.setRight(null);
    }

    public L getLeft() {
        return this.left;
    }

    public void setLeft(L left) {
        this.left = left;
    }

    public R getRight() {
        return this.right;
    }

    public void setRight(R right) {
        this.right = right;
    }
}
