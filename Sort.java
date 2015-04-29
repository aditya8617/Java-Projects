public class Sort
{
  public static void main(String[] args)
  {
    int[] list = {2, 5, 2, 6, 3, 8, 0, 7, 9, 4};//{1, 12, 5, 26, 7, 14, 3, 7, 2};//{2, 0, 6, 9, 1, 0, 8, 6, 1, 7, 2, 0, 6, 8, 9, 0, 4, 7, 6, 7};
    System.out.print("Original array = ");
    print(list);
    mergeSort(list);
    /*quickSort(list);
    insertionSort(list);
    selectionSort(list);
    bubbleSort(list);*/
  }
  
  public static void mergeSort(int[] list)
  {
    System.out.print("Merge sorted array = ");
    print(mergeSortHelper(list));
  }
  
  public static int[] mergeSortHelper(int[] list)
  {
    if (list.length <= 1) 
    {
      return list;
    }
    
    int[] first = new int[list.length / 2];
    int[] second = new int[list.length - first.length];
    System.arraycopy(list, 0, first, 0, first.length);
    System.arraycopy(list, first.length, second, 0, second.length);
    
    first = mergeSortHelper(first);
    second = mergeSortHelper(second);
    
    list = merge(first, second);
    return list;
  }
  
  private static int[] merge(int[] first, int[] second) 
  {
    int iFirst = 0;
    int iSecond = 0;
    int [] result = new int[first.length + second.length];
    
    int j = 0;
    while (iFirst < first.length && iSecond < second.length)
    {
      if (first[iFirst] < second[iSecond])
      {
        result[j] = first[iFirst];
        iFirst++;
      }
      else
      {
        result[j] = second[iSecond];
        iSecond++;
      }
      j++;
    }
    System.arraycopy(first, iFirst, result, j, first.length - iFirst);
    System.arraycopy(second, iSecond, result, j, second.length - iSecond);
    return result;
  }
  
  public static void quickSort(int[] list)
  {
    quickSortHelper(list, 0, list.length);
  }
  
  private static void quickSortHelper(int[] list, int lo, int hi)
  {
    if(lo == hi)
    {
      if(lo == list.length - 1)
      {
        System.out.print("Quick sorted array = ");
        print(list);
      }
    }
    else if(lo < hi)
    {
      int pIndex = (lo + hi)/2;
      int pivot = list[pIndex];
      int len = hi - 1;
      
      //swap pivot with last element
      list[pIndex] = list[len];
      list[len] = pivot;
      
      pIndex = lo;
      for(int i = lo; i < len; i++)
      {
        if(list[i] <= pivot)
        {
          int temp = list[pIndex];
          list[pIndex] = list[i];
          list[i] = temp;
          pIndex++;
        }
      }
      
      list[len] = list[pIndex];
      list[pIndex] = pivot;
      
      quickSortHelper(list, lo, pIndex);
      quickSortHelper(list, pIndex + 1, hi);
    }
    else
      System.out.println("Error");
  }
  
  public static void insertionSort(int[] list)
  {
    for(int i = 1; i < list.length; i++)
    {
      int subLen = i;
      while(subLen > 0)
      {
        if(list[subLen] < list[subLen - 1])
        {
          int temp = list[subLen];
          list[subLen] = list[subLen - 1];
          list[subLen - 1] = temp;
          subLen--;
        }
        else
        {
          subLen = 0;
        }
      }
    }
    System.out.print("Insertion sorted array = ");
    print(list);
  }
  public static void selectionSort(int[] list)
  {
    for(int i = 0; i < list.length; i++)
    {
      int min = list[i];
      int index = i;
      for(int j = i; j < list.length; j++)
      {
        if(min > list[j])
        {
          min = list[j];
          index = j;
        }
      }
      for(int j = index; j > i; j--)
      {
        list[j] = list[j - 1];
      }
      list[i] = min;
    }
    System.out.print("Selection sorted array = ");
    print(list);
  }
  
  public static void bubbleSort(int[] list)
  {
    boolean swap = true;
    while(swap)
    {
      swap = false;
      for(int i = 1; i < list.length; i++)
      {
        if(list[i-1] > list[i])
        {
          int temp = list[i];
          list[i] = list[i-1];
          list[i-1] = temp;
          swap = true;
        }
      }
    }
    System.out.print("Bubble sorted array = ");
    print(list);
  }
  
  public static void print(int[] list)
  {
    System.out.print("{");
    for(int i = 0; i < list.length; i++)
    {
      if(i < list.length - 1)
        System.out.print(list[i] + ", ");
      else
        System.out.println(list[i] + "}");
    }
  }
}