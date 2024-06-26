package org.rothe.john.working_hours.util;

import lombok.val;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.*;

public class Pair<T> {
    private final T left;
    private final T right;

    Pair(T left, T right) {
        this.left = left;
        this.right = right;
    }

    public T left() {
        return left;
    }

    public T right() {
        return right;
    }

    public static <T> Stream<Pair<T>> stream(List<T> list) {
        return StreamSupport.stream(spliterator(list), false);
    }

    private static <T> Spliterator<Pair<T>> spliterator(List<T> list) {
        return Spliterators.spliterator(new PairingIterator<>(list),
                list.size(), SIZED | IMMUTABLE | NONNULL | ORDERED);
    }

    private static class PairingIterator<T> implements Iterator<Pair<T>> {
        private final Iterator<T> it;
        private T left = null;

        PairingIterator(List<T> list) {
            this.it = List.copyOf(list).iterator();
            if (it.hasNext()) {
                this.left = it.next();
            }
        }

        @Override
        public Pair<T> next() {
            val pair = new Pair<>(left, it.next());
            left = pair.right();
            return pair;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }
    }
}
