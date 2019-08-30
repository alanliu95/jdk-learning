public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
        }

// 第三个参数 onlyIfAbsent 如果是 true，那么只有在不存在该 key 时才会进行 put 操作
// 第四个参数 evict 我们这里不关心
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
        boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        // 第一次 put 值的时候，会触发下面的 resize()，类似 java7 的第一次 put 也要初始化数组长度
        // 第一次 resize 和后续的扩容有些不一样，因为这次是数组从 null 初始化到默认的 16 或自定义的初始容量
        if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
        // 找到具体的数组下标，如果此位置没有值，那么直接初始化一下 Node 并放置在这个位置就可以了
        if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);

        else {// 数组该位置有数据
        Node<K,V> e; K k;
        // 首先，判断该位置的第一个数据和我们要插入的数据，key 是不是"相等"，如果是，取出这个节点
        if (p.hash == hash &&
        ((k = p.key) == key || (key != null && key.equals(k))))
        e = p;
        // 如果该节点是代表红黑树的节点，调用红黑树的插值方法，本文不展开说红黑树
        else if (p instanceof TreeNode)
        e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        else {
        // 到这里，说明数组该位置上是一个链表
        for (int binCount = 0; ; ++binCount) {
        // 插入到链表的最后面(Java7 是插入到链表的最前面)
        if ((e = p.next) == null) {
        p.next = newNode(hash, key, value, null);
        // TREEIFY_THRESHOLD 为 8，所以，如果新插入的值是链表中的第 9 个
        // 会触发下面的 treeifyBin，也就是将链表转换为红黑树
        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
        treeifyBin(tab, hash);
        break;
        }
        // 如果在该链表中找到了"相等"的 key(== 或 equals)
        if (e.hash == hash &&
        ((k = e.key) == key || (key != null && key.equals(k))))
        // 此时 break，那么 e 为链表中[与要插入的新值的 key "相等"]的 node
        break;
        p = e;
        }
        }
        // e!=null 说明存在旧值的key与要插入的key"相等"
        // 对于我们分析的put操作，下面这个 if 其实就是进行 "值覆盖"，然后返回旧值
        if (e != null) {
        V oldValue = e.value;
        if (!onlyIfAbsent || oldValue == null)
        e.value = value;
        afterNodeAccess(e);
        return oldValue;
        }
        }
        ++modCount;
        // 如果 HashMap 由于新插入这个值导致 size 已经超过了阈值，需要进行扩容
        if (++size > threshold)
        resize();
        afterNodeInsertion(evict);
        return null;
        }