package ec.kleber.tesis.tracker.ui.login

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import ec.kleber.tesis.tracker.R
import kotlinx.android.synthetic.main.login_fragment.*
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import ec.kleber.tesis.tracker.ui.MainViewModel


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        viewModel.loginSuccess.observe(this, Observer {
            it.getContentIfNotHandled()?.let {success ->
                progressBar.isIndeterminate = false
                progressBar.visibility = View.GONE
                button.visibility = View.VISIBLE
                if (success)
                {
                    ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
                            .setCurrentUserData(viewModel.currentUserId!!, viewModel.currentUserToken!!)
                }
                else
                    Snackbar.make(button, R.string.error_incorrect_password, Snackbar.LENGTH_LONG).show()
            }
        })

        button.setOnClickListener {
            progressBar.isIndeterminate = true
            progressBar.visibility = View.VISIBLE
            button.visibility = View.GONE
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            viewModel.login(textInputLayout.editText!!.text.toString(), textInputLayout2.editText!!.text.toString())
        }
    }

}
