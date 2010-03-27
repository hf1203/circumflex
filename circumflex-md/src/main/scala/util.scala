package ru.circumflex.md

import java.util.regex.{Pattern, Matcher}

/* # Character protector */

/**
 * We use character protector mechanism to ensure that certain elements of markup,
 * such as inline HTML blocks, remain undamaged when processing.
 */
class Protector {
  import Markdown._
  protected var protectHash: Map[String, CharSequence] = Map()
  protected var unprotectHash: Map[CharSequence, String] = Map()

  /**
   * Generates a random hash key.
   */
  def randomKey = (0 to keySize).foldLeft("")((s, i) =>
    s + chars.charAt(rnd.nextInt(keySize)))

  /**
   * Adds the specified token to hash and returns the protection key.
   */
  def addToken(t: CharSequence): String = {
    val key = randomKey
    protectHash += key -> t
    unprotectHash += t -> key
    return key
  }

  override def toString = protectHash.toString
}

/* # Enhanced String Buffer */

/**
 * A simple wrapper over `StringBuffer`. See, while in general
 * `StringBuilder` provides more speed, for some reason Java regular expressions
 * provide support only for buffers (that are organized in a thread-safe manner, which
 * tend to be much slower than builder). Since we use a single-thread model here, we
 * find that fact offensive to all the `StringBuilder` thang, but sincerely can't do
 * no damn thing about it.
 */
class StringEx(protected var text: StringBuffer) {

  def this(source: CharSequence) = this(new StringBuffer(source))

  /**
   * A convenient equivalent to `String.replaceAll` that accepts `Pattern`
   * instead of `String`.
   */
  def replaceAll(pattern: Pattern, replacement: String): this.type =
    replaceAll(pattern, m => replacement)

  /**
   * A convenient equivalent to `String.replaceAll` that accepts `Pattern`
   * instead of `String` and a function `Matcher => String`.
   */
  def replaceAll(pattern: Pattern, replacementFunction: Matcher => String): this.type = {
    val lastIndex = 0;
    val m = pattern.matcher(text);
    val sb = new StringBuffer();
    while (m.find()) m.appendReplacement(sb, replacementFunction(m));
    m.appendTail(sb);
    text = sb;
    return this
  }

  /**
   * Appends the specified character sequence.
   */
  def append(s: CharSequence): this.type = {
    text.append(s)
    return this
  }

  /**
   * Prepends the specified character sequence.
   */
  def prepend(s: CharSequence): this.type = {
    text = new StringBuffer(s).append(text)
    return this
  }

  /**
   * Adds the subsequence between `startIdx` and `endIdx` to specified `protector` and
   * applies a replacement to the `text`.
   */
  def protectSubseq(protector: Protector, startIdx: Int, endIdx: Int): String = {
    val subseq = text.subSequence(startIdx, endIdx)
    val key = protector.addToken(subseq)
    text = new StringBuffer(text.subSequence(0, startIdx))
        .append(key)
        .append("\n\n")
        .append(text.subSequence(endIdx, text.length))
    return key
  }

  /**
   * Provides the length of the underlying buffer.
   */
  def length = text.length

  /**
   * Extracts the sub-sequence from underlying buffer.
   */
  def subSequence(start: Int, end: Int) =
    text.subSequence(start, end)

  /**
   * Creates a `Matcher` from specified `pattern`.
   */
  def matcher(pattern: Pattern) = pattern.matcher(text)

  /**
   * Emits the content of underlying buffer.
   */
  override def toString = text.toString
}