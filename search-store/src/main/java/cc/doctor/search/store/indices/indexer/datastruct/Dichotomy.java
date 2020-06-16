package cc.doctor.search.store.indices.indexer.datastruct;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 2017/3/20.
 * 二分法
 */
public class Dichotomy {
    public static <T extends Comparable<T>> T dichotomySearch(List<T> list, T key) {
        int size = list.size();
        int left = 0;
        int middle = size / 2;
        int right = size - 1;
        while (left <= right) {
            T middleT = list.get(middle);
            if (middleT.compareTo(key) > 0) {
                right = middle - 1;
                middle = (left + right) / 2;
            } else if (middleT.compareTo(key) < 0) {
                left = middle + 1;
                middle = (left + right) / 2;
            } else {
                return middleT;
            }
        }
        return null;
    }

    public static <T extends Comparable<T>> void dichotomyInsert(List<T> list, T element) {
        int size = list.size();
        int left = 0;
        int middle = size / 2;
        int right = size - 1;
        while (left <= right) {
            T middleT = list.get(middle);
            if (middleT.compareTo(element) == 0) {
                break;
            }
            if (middle == size - 1) {
                list.add(size, element);
                break;
            }
            if (middle == 0) {
                list.add(0, element);
                break;
            }
            T middleRightT = list.get(middle + 1);
            if (middleT.compareTo(element) < 0 && middleRightT.compareTo(element) > 0) {
                list.add(middle + 1, element);
                break;
            }
            if (middleT.compareTo(element) > 0) {
                right = middle - 1;
                middle = (left + right) / 2;
            } else if (middleT.compareTo(element) < 0) {
                left = middle + 1;
                middle = (left + right) / 2;
            }
        }
    }

    public static void main(String[] args) {
        List<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(5);
        list.add(8);
        list.add(10);
        list.add(12);
        list.add(13);
        list.add(16);
        list.add(17);
        list.add(21);
        list.add(28);
        list.add(33);
        list.add(40);
        list.add(43);
        Dichotomy.dichotomyInsert(list, 23);
        System.out.println(list);
    }
}
