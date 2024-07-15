package com.yhy.jakit.util.opt;


import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 类似 jdk 自带的 Opt 功能
 * <p>
 * Created on 2024-04-19 09:50
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public class Opt<T> {

    /**
     * Common instance for {@code empty()}.
     */
    private static final Opt<?> EMPTY = new Opt<>(null);

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private final T value;

    /**
     * Returns an empty {@code Opt} instance.  No value is present for this
     * {@code Opt}.
     *
     * @param <T> The type of the non-existent value
     * @return an empty {@code Opt}
     * @apiNote Though it may be tempting to do so, avoid testing if an object is empty
     * by comparing with {@code ==} or {@code !=} against instances returned by
     * {@code Opt.empty()}.  There is no guarantee that it is a singleton.
     * Instead, use {@link #isEmpty()} or {@link #isPresent()}.
     */
    public static <T> Opt<T> empty() {
        @SuppressWarnings("unchecked")
        Opt<T> t = (Opt<T>) EMPTY;
        return t;
    }

    /**
     * Constructs an instance with the described value.
     *
     * @param value the value to describe; it's the caller's responsibility to
     *              ensure the value is non-{@code null} unless creating the singleton
     *              instance returned by {@code empty()}.
     */
    private Opt(T value) {
        this.value = value;
    }

    /**
     * Returns an {@code Opt} describing the given non-{@code null}
     * value.
     *
     * @param value the value to describe, which must be non-{@code null}
     * @param <T>   the type of the value
     * @return an {@code Opt} with the value present
     * @throws NullPointerException if value is {@code null}
     */
    public static <T> Opt<T> of(T value) {
        return new Opt<>(Objects.requireNonNull(value));
    }

    /**
     * Returns an {@code Opt} describing the given value, if
     * non-{@code null}, otherwise returns an empty {@code Opt}.
     *
     * @param value the possibly-{@code null} value to describe
     * @param <T>   the type of the value
     * @return an {@code Opt} with a present value if the specified value
     * is non-{@code null}, otherwise an empty {@code Opt}
     */
    @SuppressWarnings("unchecked")
    public static <T> Opt<T> ofNullable(T value) {
        return value == null ? (Opt<T>) EMPTY : new Opt<>(value);
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return the non-{@code null} value described by this {@code Opt}
     * @throws NoSuchElementException if no value is present
     * @apiNote The preferred alternative to this method is {@link #orElseThrow()}.
     */
    public T get() {
        if (!isPresent()) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * If a value is present, returns {@code true}, otherwise {@code false}.
     *
     * @return {@code true} if a value is present, otherwise {@code false}
     */
    public boolean isPresent() {
        if (null != value) {
            if (value instanceof CharSequence) {
                CharSequence text = ((CharSequence) value);
                return text.length() > 0;
            }
            if (value instanceof Iterable) {
                Iterable<?> iterable = ((Iterable<?>) value);
                return iterable.iterator().hasNext();
            }
            if (value instanceof Iterator) {
                Iterator<?> iterator = ((Iterator<?>) value);
                return iterator.hasNext();
            }
            if (value instanceof Map) {
                Map<?, ?> map = ((Map<?, ?>) value);
                return !map.isEmpty();
            }
            if (value.getClass().isArray()) {
                return Array.getLength(value) > 0;
            }
            return true;
        }
        return false;
    }

    /**
     * If a value is  not present, returns {@code true}, otherwise
     * {@code false}.
     *
     * @return {@code true} if a value is not present, otherwise {@code false}
     * @since 11
     */
    public boolean isEmpty() {
        return !isPresent();
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise does nothing.
     *
     * @param consumer the consumer function to apply to a value, if present
     * @throws NullPointerException if value is present and the given action is
     *                              {@code null}
     */
    public void ifPresent(Consumer<T> consumer) {
        Objects.requireNonNull(consumer);
        if (isPresent()) {
            consumer.accept(value);
        }
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise does nothing.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @return current opt with another value
     * @throws NullPointerException if value is present and the given action is
     *                              {@code null}
     */
    public <U> Opt<U> ifPresentOf(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return isPresent() ? ofNullable(mapper.apply(value)) : empty();
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise performs the given empty-based action.
     *
     * @param mapper   the mapping function to apply to a value, if present
     * @param supplier the empty-based action to be performed, if no value is
     *                 present
     * @throws NullPointerException if a value is present and the given action
     *                              is {@code null}, or no value is present and the given empty-based
     *                              action is {@code null}.
     * @since 9
     */
    public <U> Opt<U> ifPresentOrElse(Function<? super T, ? extends U> mapper, Supplier<? extends U> supplier) {
        return isPresent() ? ofNullable(mapper.apply(value)) : ofNullable(supplier.get());
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * returns an {@code Opt} describing the value, otherwise returns an
     * empty {@code Opt}.
     *
     * @param predicate the predicate to apply to a value, if present
     * @return an {@code Opt} describing the value of this
     * {@code Opt}, if a value is present and the value matches the
     * given predicate, otherwise an empty {@code Opt}
     * @throws NullPointerException if the predicate is {@code null}
     */
    public Opt<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return isEmpty() ? this : predicate.test(value) ? this : empty();
    }

    /**
     * If a value is present, returns an {@code Opt} describing (as if by
     * {@link #ofNullable}) the result of applying the given mapping function to
     * the value, otherwise returns an empty {@code Opt}.
     *
     * <p>If the mapping function returns a {@code null} result then this method
     * returns an empty {@code Opt}.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @param <U>    The type of the value returned from the mapping function
     * @return an {@code Opt} describing the result of applying a mapping
     * function to the value of this {@code Opt}, if a value is
     * present, otherwise an empty {@code Opt}
     * @throws NullPointerException if the mapping function is {@code null}
     * @apiNote This method supports post-processing on {@code Opt} values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of URIs, selects one that has not
     * yet been processed, and creates a path from that URI, returning
     * an {@code Opt<Path>}:
     *
     * <pre>{@code
     *     Opt<Path> p =
     *         uris.stream().filter(uri -> !isProcessedYet(uri))
     *                       .findFirst()
     *                       .map(Paths::get);
     * }</pre>
     * <p>
     * Here, {@code findFirst} returns an {@code Opt<URI>}, and then
     * {@code map} returns an {@code Opt<Path>} for the desired
     * URI if one exists.
     */
    public <U> Opt<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (isEmpty()) {
            return empty();
        } else {
            return Opt.ofNullable(mapper.apply(value));
        }
    }

    /**
     * If a value is present, returns the result of applying the given
     * {@code Opt}-bearing mapping function to the value, otherwise returns
     * an empty {@code Opt}.
     *
     * <p>This method is similar to {@link #map(Function)}, but the mapping
     * function is one whose result is already an {@code Opt}, and if
     * invoked, {@code flatMap} does not wrap it within an additional
     * {@code Opt}.
     *
     * @param <U>    The type of value of the {@code Opt} returned by the
     *               mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return the result of applying an {@code Opt}-bearing mapping
     * function to the value of this {@code Opt}, if a value is
     * present, otherwise an empty {@code Opt}
     * @throws NullPointerException if the mapping function is {@code null} or
     *                              returns a {@code null} result
     */
    public <U> Opt<U> flatMap(Function<? super T, ? extends Opt<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (isEmpty()) {
            return empty();
        } else {
            @SuppressWarnings("unchecked")
            Opt<U> r = (Opt<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        }
    }

    /**
     * If a value is present, returns an {@code Opt} describing the value,
     * otherwise returns an {@code Opt} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code Opt}
     *                 to be returned
     * @return returns an {@code Opt} describing the value of this
     * {@code Opt}, if a value is present, otherwise an
     * {@code Opt} produced by the supplying function.g
     * @throws NullPointerException if the supplying function is {@code null} or
     *                              produces a {@code null} result
     * @since 9
     */
    public Opt<T> or(Supplier<? extends Opt<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (isPresent()) {
            return this;
        } else {
            @SuppressWarnings("unchecked")
            Opt<T> r = (Opt<T>) supplier.get();
            return Objects.requireNonNull(r);
        }
    }

    /**
     * If a value is present, returns an {@code Opt} describing the value,
     * otherwise returns an {@code Opt} produced by the supplying function.
     *
     * @param runnable the supplying function that produces an {@code Opt} to be returned
     * @return returns an {@code Opt} describing the value of this
     * {@code Opt}, if a value is present, otherwise an
     * {@code Opt} produced by the supplying function.g
     * @throws NullPointerException if the supplying function is {@code null} or
     *                              produces a {@code null} result
     * @since 9
     */
    public Opt<T> orEmpty(Runnable runnable) {
        Objects.requireNonNull(runnable);
        if (isPresent()) {
            return this;
        } else {
            runnable.run();
            return empty();
        }
    }

    /**
     * If a value is present, returns a sequential {@link Stream} containing
     * only that value, otherwise returns an empty {@code Stream}.
     *
     * @return the optional value as a {@code Stream}
     * @apiNote This method can be used to transform a {@code Stream} of optional
     * elements to a {@code Stream} of present value elements:
     * <pre>{@code
     *     Stream<Opt<T>> os = ..
     *     Stream<T> s = os.flatMap(Opt::stream)
     * }</pre>
     * @since 9
     */
    public Stream<T> stream() {
        if (isEmpty()) {
            return Stream.empty();
        } else {
            return Stream.of(value);
        }
    }

    /**
     * If a value is present, returns the value, otherwise returns
     * {@code other}.
     *
     * @param other the value to be returned, if no value is present.
     *              May be {@code null}.
     * @return the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
        return isPresent() ? value : other;
    }

    /**
     * If a value is present, returns the value, otherwise returns the result
     * produced by the supplying function.
     *
     * @param supplier the supplying function that produces a value to be returned
     * @return the value, if present, otherwise the result produced by the
     * supplying function
     * @throws NullPointerException if no value is present and the supplying
     *                              function is {@code null}
     */
    public T orElseGet(Supplier<? extends T> supplier) {
        return isPresent() ? value : supplier.get();
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return the non-{@code null} value described by this {@code Opt}
     * @throws NoSuchElementException if no value is present
     * @since 10
     */
    public T orElseThrow() {
        if (!isPresent()) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @param msg the error message
     * @return the non-{@code null} value described by this {@code Opt}
     * @throws NoSuchElementException if no value is present
     * @since 10
     */
    public T orElseThrow(String msg) {
        if (!isPresent()) {
            throw new IllegalStateException(msg);
        }
        return value;
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @param e the error
     * @return the non-{@code null} value described by this {@code Opt}
     * @throws NoSuchElementException if no value is present
     * @since 10
     */
    public T orElseThrow(RuntimeException e) {
        if (!isPresent()) {
            throw e;
        }
        return value;
    }

    /**
     * If a value is present, returns the value, otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @param <X>               Type of the exception to be thrown
     * @param exceptionSupplier the supplying function that produces an
     *                          exception to be thrown
     * @return the value, if present
     * @throws X                    if no value is present
     * @throws NullPointerException if no value is present and the exception
     *                              supplying function is {@code null}
     * @apiNote A method reference to the exception constructor with an empty argument
     * list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isPresent()) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Indicates whether some other object is "equal to" this {@code Opt}.
     * The other object is considered equal if:
     * <ul>
     * <li>it is also an {@code Opt} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {@code true} if the other object is "equal to" this object
     * otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Opt)) {
            return false;
        }
        Opt<?> other = (Opt<?>) obj;
        return Objects.equals(value, other.value);
    }

    /**
     * Returns the hash code of the value, if present, otherwise {@code 0}
     * (zero) if no value is present.
     *
     * @return hash code value of the present value or {@code 0} if no value is
     * present
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Returns a non-empty string representation of this {@code Opt}
     * suitable for debugging.  The exact presentation format is unspecified and
     * may vary between implementations and versions.
     *
     * @return the string representation of this instance
     * @implSpec If a value is present the result must include its string representation
     * in the result.  Empty and present {@code Opt}s must be unambiguously
     * differentiable.
     */
    @Override
    public String toString() {
        return isPresent() ? ("Opt[" + value + "]") : "Opt.empty";
    }

    // ------------------------------------------------------------- 以下为增强的 Optional 功能，主要支持集合类型 -------------------------------------------------------------

    public static <T, O extends Iterable<T>> Opt<O> ofEmpty(O elements) {
        return ofNullable(elements);
    }

    public static <T, O extends Iterator<T>> Opt<O> ofEmpty(O elements) {
        return ofNullable(elements);
    }

    public static <K, V, O extends Map<K, V>> Opt<O> ofEmpty(O elements) {
        return ofNullable(elements);
    }

    public static <S extends CharSequence> Opt<S> ofEmpty(S text) {
        return ofNullable(text);
    }

    public static <T> Opt<T[]> ofEmpty(T[] ts) {
        return ofNullable(ts);
    }
}
