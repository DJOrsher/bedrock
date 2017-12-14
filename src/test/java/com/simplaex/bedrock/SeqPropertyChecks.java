package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.HashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings("CodeBlock2Expr")
@UtilityClass
class SeqPropertyChecks {

  void checks(final Seq<Integer> seq) {

    describe("length + isEmpty", () -> {
      it("length() > 0 == !isEmpty()", () -> expect(seq.length() > 0 == !seq.isEmpty()).toBeTrue());
      it("length() == 0 == isEmpty()", () -> expect(seq.length() == 0 == seq.isEmpty()).toBeTrue());
    });

    describe("builder + forEach", () -> {
      it("iterating and building a new Seq should yield the same seq", () -> {
        val b = Seq.builder();
        seq.forEach(b::add);
        val s = b.result();
        expect(s).toEqual(seq);
      });
    });

    describe("shuffled", () -> {
      it("should contain the same elements as before", () -> {
        val s = seq.shuffled();
        for (val e : seq) {
          expect(s.contains(e)).toBeTrue();
        }
      });
      it("should contain the same elements the same number of times as before", () -> {
        val s = seq.shuffled();
        for (val e : seq) {
          expect(s.count(e)).toEqual(seq.count(e));
        }
      });
      it("the shuffled result should have the same length as before", () -> {
        expect(seq.shuffled().length()).toEqual(seq.length());
      });
    });

    describe("sorted", () -> {
      it("should contain the same elements as before", () -> {
        val s = seq.sorted();
        for (val e : seq) {
          expect(s.contains(e)).toBeTrue();
        }
      });
      it("should contain the same elements the same number of times as before", () -> {
        val s = seq.sorted();
        for (val e : seq) {
          expect(s.count(e)).toEqual(seq.count(e));
        }
      });
      it("the shuffled result should have the same length as before", () -> {
        expect(seq.sorted().length()).toEqual(seq.length());
      });
      it("should be ordered", () -> {
        val s = seq.sorted();
        val z = seq.zipWith((a, b) -> a <= b, s, s.tail());
        expect(z.forAll(x -> x)).toBeTrue();
      });
    });

    describe("stream", () -> {
      it("collecting the stream to a list and creating a seq out of it should be the same seq", () -> {
        expect(Seq.ofCollection(seq.stream().collect(Collectors.toList()))).toEqual(seq);
      });
    });

    describe("equals", () -> {
      it("should equal itself", () -> {
        expect(seq).toEqual(seq);
      });
      it("should not equal null", () -> {
        //noinspection ObjectEqualsNull
        expect(seq.equals(null)).toBeFalse();
      });
    });

    describe("hashCode", () -> {
      it("should be usable as key in a HashMap", () -> {
        val map = new HashMap<Seq<Integer>, String>();
        map.put(seq, "quux");
        expect(map.get(seq)).toEqual("quux");
      });
    });

    if (!seq.isEmpty()) {
      describe("head + get", () -> {
        it("should return the first element", () -> expect(seq.head()).toEqual(seq.get(0)));
      });

      describe("last + get", () -> {
        it("should return the last element", () -> expect(seq.last()).toEqual(seq.get(seq.size() - 1)));
      });

      describe("sorted + contains", () -> {
        it("should contain the elements it did before sorting", () -> {
          val s = seq.sorted();
          seq.forEach(e -> expect(s.contains(e)).toBeTrue());
        });
      });
    }

    describe("partition + filter + filterNot", () -> {
      it("should partition into filter and filterNot", () -> {
        class F implements Predicate {
          private int x = 0;
          @Override
          public boolean test(final Object ignored) {
            return (x++) % 2 == 0;
          }
        }
        //noinspection unchecked
        val r = seq.partitionBy(new F());
        //noinspection unchecked
        expect(r.fst).toEqual(seq.filter(new F()));
        //noinspection unchecked
        expect(r.snd).toEqual(seq.filterNot(new F()));
      });
    });

    describe("builder", () -> {
      it("Adding all elements from toList to a builder should yield the same seq", () -> {
        expect(Seq.builder().addElements(seq.toList()).result()).toEqual(seq);
      });
    });

  }

}
