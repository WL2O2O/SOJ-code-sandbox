class Solution {
    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer>  hashMap = new HashMap<>();
        for(int i = 0; i < nums.length; i++) {  
            int diff = target - nums[i];
            if(hashMap.containsKey(diff)) {
                return new int[] {i, hashMap.get(diff)};
            } hashMap.put(nums[i], i);

        }
        return new int[] {0,0};
    }
}