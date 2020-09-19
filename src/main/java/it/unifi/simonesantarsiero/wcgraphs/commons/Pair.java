package it.unifi.simonesantarsiero.wcgraphs.commons;

public class Pair<A extends Comparable<A>, B extends Comparable<B>> implements Comparable<Pair<A, B>>{
	private A first;
	private B second;

	public Pair(A first, B second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public int hashCode() {
		int hashFirst = first != null ? first.hashCode() : 0;
		int hashSecond = second != null ? second.hashCode() : 0;

		return (hashFirst + hashSecond) * hashSecond + hashFirst;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Pair<?, ?>) {
			Pair<?, ?> p = (Pair<?, ?>)other;
			return (p.first == first) &&
					(p.second == second);
		}
		return false;
	}

	@Override
	public String toString()
	{
		return "(" + first + ", " + second + ")";
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}

	@Override
	public int compareTo(Pair<A, B> p) {
		return first.compareTo(p.getFirst());
	}
}