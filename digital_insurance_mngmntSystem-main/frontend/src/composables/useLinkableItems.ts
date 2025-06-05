import { ref, onMounted, watch } from 'vue';
import { fetchUserPolicies, fetchUserClaims } from '../services/api';
import type { UserPolicy, Claim } from '../types/support';

export function useLinkableItems() {
  const linkPolicy = ref(false);
  const linkClaim = ref(false);
  const selectedPolicyId = ref('');
  const selectedClaimId = ref('');

  const userPolicies = ref<UserPolicy[]>([]);
  const userClaims = ref<Claim[]>([]);
  const loadingData = ref(true);
  const dataError = ref('');

  watch(linkPolicy, (isLinked) => {
    if (!isLinked) {
      selectedPolicyId.value = '';
    }
  });

  watch(linkClaim, (isLinked) => {
    if (!isLinked) {
      selectedClaimId.value = '';
    }
  });

  const fetchAllUserData = async () => {
    loadingData.value = true;
    dataError.value = '';
    try {
      const [policies, claims] = await Promise.all([
        fetchUserPolicies(),
        fetchUserClaims(),
      ]);
      userPolicies.value = policies;
      userClaims.value = claims;
    } catch (err: any) {
      console.error('Error fetching user data:', err);
      dataError.value = '';
    } finally {
      loadingData.value = false;
    }
  };


  const resetLinkableItems = () => {
    linkPolicy.value = false;
    linkClaim.value = false;
    selectedPolicyId.value = ''
    selectedClaimId.value = '';
  };

  onMounted(() => {
    fetchAllUserData();
  });

  return {
    linkPolicy,
    linkClaim,
    selectedPolicyId,
    selectedClaimId,
    userPolicies,
    userClaims,
    loadingData,
    dataError,
    fetchAllUserData, // Expose if manual refresh is needed
    resetLinkableItems,
  };
}
