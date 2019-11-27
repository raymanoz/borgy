import {History} from "history";
import React, {Component} from "react";
import {connect} from "react-redux";
import {NavLink} from "react-router-dom";
import {AppState} from "../store";
import {fetchScales} from "../store/scales/operations";
import {server} from "../utils/server";
import "./Home.css";
import NewTrial from "./home/NewTrial";
import TrialsComponent from "./home/Trials";
import {Trials} from "./Scale";

interface Props {
    history: History;
}

interface State {
    trials: Trials;
}

class Home extends Component<Props, State> {
    constructor(props: Readonly<Props>) {
        super(props);
        this.state = {
            trials: [],
        };
        this.renderActiveTrial = this.renderActiveTrial.bind(this);
        this.completeTrial = this.completeTrial.bind(this);
    }

    public componentDidMount() {
        this.refreshTrials();
    }

    public render() {
        return <main>
            <NewTrial history={this.props.history}/>
            <TrialsComponent/>
        </main>;
    }

    private refreshTrials() {
        fetch(server.trials)
            .then((result) => result.json())
            .then((t) => this.setState({trials: t}));
    }

    private renderActiveTrial(trial: string, idx: number) {
        return <div key={idx} className="row active-trial">
            <div className="col-4"><NavLink to={"/trial/" + trial}>{trial}</NavLink></div>
            <button className="btn btn-danger" onClick={(_) => this.completeTrial(trial)}>Complete</button>
        </div>;
    }

    private completeTrial(trialName: string) {
        fetch(server.trial(trialName), {method: "DELETE", credentials: "same-origin"})
            .then((_) => this.refreshTrials());
    }
}

const mapStateToProps = (state: AppState) => ({
    scales: state.scales,
});

export default connect(
    mapStateToProps,
    { fetchScales },
)(Home);
