package xyz.hisname.fireflyiii.ui.piggybank

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.fragment_piggy_detail.*
import kotlinx.android.synthetic.main.progress_overlay.*
import kotlinx.coroutines.*
import xyz.hisname.fireflyiii.R
import xyz.hisname.fireflyiii.repository.models.BaseDetailModel
import xyz.hisname.fireflyiii.repository.models.piggy.PiggyAttributes
import xyz.hisname.fireflyiii.repository.viewmodel.PiggyBankViewModel
import xyz.hisname.fireflyiii.ui.ProgressBar
import xyz.hisname.fireflyiii.ui.base.BaseDetailFragment
import xyz.hisname.fireflyiii.ui.base.BaseDetailRecyclerAdapter
import xyz.hisname.fireflyiii.util.extension.*
import java.math.BigDecimal
import kotlin.collections.ArrayList

class PiggyDetailFragment: BaseDetailFragment(), CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.Main
    private val piggyBankViewModel by lazy { getViewModel(PiggyBankViewModel::class.java)}
    private val piggyId: Long by lazy { arguments?.getLong("piggyId") as Long  }
    private var piggyAttribute: PiggyAttributes? = null
    private var currentAmount: BigDecimal? = 0.toBigDecimal()
    private var percentage: Int = 0
    private var currencyCode: String = ""
    private var piggyList: MutableList<BaseDetailModel> = ArrayList()
    private var piggyName: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.create(R.layout.fragment_piggy_detail,container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = LinearLayoutManager(requireContext())
        launch(context = Dispatchers.Main){
            val result = async(Dispatchers.IO) {
                piggyBankViewModel.getPiggyBankById(piggyId)
            }.await()
            piggyAttribute = result!![0].piggyAttributes
            piggyName = piggyAttribute?.name
            currentAmount = piggyAttribute?.current_amount
            percentage = piggyAttribute!!.percentage
            currencyCode = piggyAttribute!!.currency_code
            setupWidgets()
            recycler_view.adapter = BaseDetailRecyclerAdapter(piggyList)
            setupProgressBar()
        }
    }


    private fun setupWidgets(){
        val piggy = arrayListOf(
                BaseDetailModel("Created At", piggyAttribute?.created_at,
                        IconicsDrawable(requireContext()).icon(GoogleMaterial.Icon.gmd_create).sizeDp(24)),
                BaseDetailModel("Updated At", piggyAttribute?.updated_at,
                        IconicsDrawable(requireContext()).icon(GoogleMaterial.Icon.gmd_update).sizeDp(24)),
                BaseDetailModel("Left To Save", piggyAttribute?.left_to_save.toString(),
                        IconicsDrawable(requireContext()).icon(GoogleMaterial.Icon.gmd_save).sizeDp(24)),
                BaseDetailModel("Save Per Month", piggyAttribute?.save_per_month.toString(),
                        IconicsDrawable(requireContext()).icon(GoogleMaterial.Icon.gmd_save).sizeDp(24)),
                if (piggyAttribute?.start_date != null) {
                    BaseDetailModel("Start Date", piggyAttribute?.start_date,
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar_blank))
                } else {
                    BaseDetailModel("Start Date", "No Date Set",
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar_blank))
                },
                if (piggyAttribute?.target_date != null) {
                    BaseDetailModel("Target Date", piggyAttribute?.target_date,
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar_blank))
                } else {
                    BaseDetailModel("Target Date", "No Date Set",
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar_blank))
                },
                BaseDetailModel("Notes", piggyAttribute?.notes,
                        IconicsDrawable(requireContext()).icon(GoogleMaterial.Icon.gmd_note).sizeDp(24))
        )
        piggyList.addAll(piggy)
        if(percentage <= 15){
            piggyBankProgressBar.progressDrawable.setColorFilter(ContextCompat.getColor(requireContext(),
                    R.color.md_red_700), PorterDuff.Mode.SRC_IN)
        } else if(percentage <= 50){
            piggyBankProgressBar.progressDrawable.setColorFilter(ContextCompat.getColor(requireContext(),
                    R.color.md_green_500), PorterDuff.Mode.SRC_IN)
        }
        amount.text = currentAmount.toString()
        amountPercentage.text = percentage.toString() + "%"
        currencyCodeTextView.text = currencyCode
        piggyBankName.text = piggyName
    }

    override fun deleteItem() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.get_confirmation)
                .setMessage(R.string.irreversible_action)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    ProgressBar.animateView(progress_overlay, View.VISIBLE, 0.4f, 200)
                    piggyBankViewModel.deletePiggyBank(baseUrl,accessToken, piggyId.toString()).observe(this, Observer {
                        ProgressBar.animateView(progress_overlay, View.GONE, 0f, 200)
                        if(it.getError() == null){
                            toastSuccess(resources.getString(R.string.piggy_bank_deleted), Toast.LENGTH_LONG)
                            requireFragmentManager().popBackStack()
                        } else {
                            Snackbar.make(requireActivity().findViewById(R.id.coordinatorlayout),
                                    R.string.generic_delete_error, Snackbar.LENGTH_LONG)
                                    .setAction("Retry") { _ ->
                                        deleteItem()
                                    }
                                    .show()

                        }
                    })
                }
                .setNegativeButton(android.R.string.no){dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }


    private fun setupProgressBar(){
        ObjectAnimator.ofInt(piggyBankProgressBar, "progress", 0, percentage).apply {
            // 1000ms = 1s
            duration = 1000
            interpolator = AccelerateInterpolator()
        }.start()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId){
        R.id.menu_item_edit -> consume {
            val bundle = bundleOf("piggyId" to piggyId)
            val addPiggy = Intent(requireContext(), AddPiggyActivity::class.java).apply{
                putExtras(bundle)
            }
            startActivity(addPiggy)
        }
        R.id.menu_item_delete -> consume {
            deleteItem()
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        piggyList.clear()
    }
}