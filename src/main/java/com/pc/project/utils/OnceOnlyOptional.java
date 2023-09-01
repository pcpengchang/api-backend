package com.pc.project.utils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 为了解决Optional内部创建对象过多的问题,消除了转换过程中的新建对象,复用了存储空间,只会保留最后一次结果
 * 缺点也是特性,如果不注意用错,可能会取错值
 */
public class OnceOnlyOptional<T> {
    private static final OnceOnlyOptional<?> EMPTY = new OnceOnlyOptional<>();

    private T value;

    private OnceOnlyOptional() {
        this.value = null;
    }

    private OnceOnlyOptional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    public static <T> OnceOnlyOptional<T> empty() {
        @SuppressWarnings("unchecked")
        OnceOnlyOptional<T> t = (OnceOnlyOptional<T>) EMPTY;
        return t;
    }

    public static <T> OnceOnlyOptional<T> of(T value) {
        return new OnceOnlyOptional<>(value);
    }

    public static <T> OnceOnlyOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    public static <C> OnceOnlyOptional<C> convertToOneUse(Optional<C> optional) {
        if (optional.isPresent()) {
            return OnceOnlyOptional.of(optional.get());
        } else {
            return empty();
        }
    }

    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public OnceOnlyOptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent()) {
            return this;
        } else {
            return predicate.test(value) ? this : empty();
        }
    }

    @SuppressWarnings("unchecked")
    public <U> OnceOnlyOptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            // 此处替换掉了新对象生成
            U newValue = mapper.apply(value);
            this.value = (T) newValue;
            return (OnceOnlyOptional<U>) this;
        }
    }

    public <U> OnceOnlyOptional<U> flatMap(Function<? super T, OnceOnlyOptional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty();
        } else {
            return Objects.requireNonNull(mapper.apply(value));
        }
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }

    public T orElseGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof OnceOnlyOptional)) {
            return false;
        }

        OnceOnlyOptional<?> other = (OnceOnlyOptional<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value != null
                ? String.format("OneUseOptional[%s]", value)
                : "OneUseOptional.empty";
    }

    public Optional<T> toOptional() {
        if (isPresent()) {
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }
}
